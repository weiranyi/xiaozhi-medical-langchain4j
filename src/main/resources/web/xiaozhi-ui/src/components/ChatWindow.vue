<template>
  <div class="app-layout">
    <div class="sidebar">
      <div class="logo-section">
        <img src="../assets/logo.png" alt="硅谷小智" width="160" height="160" />
        <span class="logo-text">硅谷小智（医疗版）</span>
      </div>
      <el-button class="new-chat-button" @click="newChat">
        <i class="fa-solid fa-plus"></i>
        &nbsp;新会话
      </el-button>
    </div>
    <div class="main-content">
      <div class="chat-container">
        <div class="message-list" ref="messaggListRef">
          <div
            v-for="(message, index) in messages"
            :key="index"
            :class="
              message.isUser ? 'message user-message' : 'message bot-message'
            "
          >
            <!-- 会话图标 -->
            <i
              :class="
                message.isUser
                  ? 'fa-solid fa-user message-icon'
                  : 'fa-solid fa-robot message-icon'
              "
            ></i>
            <!-- 会话内容 -->
            <span>
              <span v-html="message.htmlContent"></span>
              <!-- loading -->
              <span
                class="loading-dots"
                v-if="message.isThinking || message.isTyping"
              >
                <span class="dot"></span>
                <span class="dot"></span>
              </span>
            </span>
            <!-- <div class="message-content" v-html="message.htmlContent"></div> -->
          </div>
        </div>
        <div class="input-container">
          <el-input
            v-model="inputMessage"
            placeholder="请输入消息"
            @keyup.enter="sendMessage"
          ></el-input>
          <el-button @click="sendMessage" :disabled="isSending" type="primary"
            >发送</el-button
          >
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref, watch } from 'vue'
import axios from 'axios'
import { v4 as uuidv4 } from 'uuid'
// 导入marked库
import {marked} from 'marked';

const messaggListRef = ref()
const isSending = ref(false)
const uuid = ref()
const inputMessage = ref('')
const messages = ref([])
// 语音朗读相关变量
const speechSynthesis = window.speechSynthesis
const speechQueue = ref([])
const isSpeaking = ref(false)

onMounted(() => {
  initUUID()
  watch(messages, () => scrollToBottom(), { deep: true })
  hello()
})

onUnmounted(() => {
  // 组件卸载时清除语音
  if (speechSynthesis) speechSynthesis.cancel()
})

const scrollToBottom = () => {
  if (messaggListRef.value) {
    messaggListRef.value.scrollTop = messaggListRef.value.scrollHeight
  }
}

const hello = () => {
  sendRequest('你好')
}

// 在创建新消息时添加htmlContent属性
const sendMessage = () => {
  if (inputMessage.value.trim()) {
    sendRequest(inputMessage.value.trim())
    inputMessage.value = ''
  }
}

// 新增延迟处理定时器
const speechTimer = ref(null)

const sendRequest = (message) => {
  isSending.value = true
  const userMsg = {
    isUser: true,
    content: message,
    htmlContent: marked.parse(message),
    isTyping: false,
    isThinking: false,
  }
  if(messages.value.length > 0){
    messages.value.push(userMsg)
  }

  const botMsg = {
    isUser: false,
    content: '',
    htmlContent: '',
    isTyping: true,
    isThinking: false,
  }
  messages.value.push(botMsg)
  const lastMsg = messages.value[messages.value.length - 1]
  scrollToBottom()

  axios
    .post(
      '/api/xiaozhi/chat',
      { memoryId: uuid.value, message },
      {
        responseType: 'stream',
        onDownloadProgress: (e) => {
          const fullText = e.event.target.responseText
          let newText = fullText.substring(lastMsg.content.length)
          lastMsg.content += newText
          lastMsg.htmlContent = marked(lastMsg.content);
          
          // 实时语音朗读 - 立即朗读新增内容
          if (newText.trim()) {
            speechQueue.value.push(newText)
            // 清除之前的定时器
            clearTimeout(speechTimer.value)
            // 设置 500ms 延迟后处理语音队列
            speechTimer.value = setTimeout(() => {
              if (!isSpeaking.value) {
                processSpeechQueue()
              }
            }, 500)
          }
          scrollToBottom()
        },
      }
    )
    .then(() => {
      messages.value.at(-1).isTyping = false
      isSending.value = false
    })
    .catch((error) => {
      console.error('流式错误:', error)
      messages.value.at(-1).content = '请求失败，请重试'
      messages.value.at(-1).isTyping = false
      isSending.value = false
    })
}

// 修改语音朗读函数
const processSpeechQueue = () => {
  if (speechQueue.value.length > 0 && !isSpeaking.value) {
    isSpeaking.value = true
    // 合并队列中的所有文本
    const textToSpeak = speechQueue.value.join('')
    speechQueue.value = []
    
    const utterance = new SpeechSynthesisUtterance(textToSpeak)
    utterance.lang = 'zh-CN'
    utterance.rate = 1.2
    utterance.pitch = 1.0
    utterance.volume = 1.0
    
    utterance.onend = () => {
      isSpeaking.value = false
      // 如果队列中又有新内容，继续处理
      if (speechQueue.value.length > 0) {
        processSpeechQueue()
      }
    }
    
    utterance.onerror = () => {
      isSpeaking.value = false
      if (speechQueue.value.length > 0) {
        processSpeechQueue()
      }
    }
    
    speechSynthesis.speak(utterance)
  }
}

// 移除旧的语音朗读相关函数：speakText, speakTextIncremental
// 移除旧的变量：speechTimer, lastSpokenIndex
// 初始化 UUID
const initUUID = () => {
  let storedUUID = localStorage.getItem('user_uuid')
  if (!storedUUID) {
    storedUUID = uuidToNumber(uuidv4())
    localStorage.setItem('user_uuid', storedUUID)
  }
  uuid.value = storedUUID
}

const uuidToNumber = (uuid) => {
  let number = 0
  for (let i = 0; i < uuid.length && i < 6; i++) {
    const hexValue = uuid[i]
    number = number * 16 + (parseInt(hexValue, 16) || 0)
  }
  return number % 1000000
}

// 转换特殊字符
const convertStreamOutput = (output) => {
  return output
    .replace(/\n/g, '<br>')
    .replace(/\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;')
    .replace(/&/g, '&amp;') // 新增转义，避免 HTML 注入
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
}

const newChat = () => {
  // 这里添加新会话的逻辑
  console.log('开始新会话')
  localStorage.removeItem('user_uuid')
  window.location.reload()
}
</script>
<style scoped>
.app-layout {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 200px;
  background-color: #f4f4f9;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo-text {
  font-size: 18px;
  font-weight: bold;
  margin-top: 10px;
}

.new-chat-button {
  width: 100%;
  margin-top: 20px;
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  background-color: #fff;
  margin-bottom: 10px;
  display: flex;
  flex-direction: column;
}

.input-container {
  display: flex;
  /* 确保输入框容器不会被挤压 */
  flex-shrink: 0;
}

/* 媒体查询，当设备宽度小于等于 768px 时应用以下样式 */ 
@media (max-width: 768px) {
  .main-content {
    padding: 10px 0 10px 0;
  }
  .app-layout {
    flex-direction: column;
  }

  .sidebar {
    /* display: none; */
    width: 100%;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
  }

  .logo-section {
    flex-direction: row;
    align-items: center;
  }

  .logo-text {
    font-size: 20px;
  }

  .logo-section img {
    width: 40px;
    height: 40px;
  }

  .new-chat-button {
    margin-right: 30px;
    width: auto;
    margin-top: 5px;
  }

  /* 新增移动端输入框和发送按钮样式 */ 
  .input-container {
    padding: 0 10px 10px 10px;
    /* 新增上边距，将输入框上移 */ 
    margin-bottom: 50vw;
  }

  .input-container .el-input {
    flex: 1;
    margin-right: 5px;
  }
}

/* 媒体查询，当设备宽度大于 768px 时应用原来的样式 */
@media (min-width: 769px) {
  .main-content {
    padding: 0 0 10px 10px;
  }

  .app-layout {
    display: flex;
    height: 100vh;
  }

  .sidebar {
    width: 200px;
    background-color: #f4f4f9;
    padding: 20px;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .logo-section {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .logo-text {
    font-size: 18px;
    font-weight: bold;
    margin-top: 10px;
  }

  .new-chat-button {
    width: 100%;
    margin-top: 20px;
  }
}
</style>
