package com.atguigu.java.ai.langchain4j.assistant;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

//因为我们在配置文件中同时配置了多个大语言模型，所以需要在这里明确指定（EXPLICIT）模型的beanName（qwenChatModel）
@AiService(
        wiringMode = EXPLICIT,
        chatModel = "ollamaChatModel",
//        chatModel = "qwenChatModel",
        chatMemory = "chatMemory")
public interface MemoryChatAssistant {
    @UserMessage("你是我的好朋友，请用上海话回答问题，并且添加一些表情符号。 {{it}}") //{{it}}表示这里唯一的参数的占位符
    String chat(String userMessage);
}