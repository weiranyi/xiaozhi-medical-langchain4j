package com.atguigu.java.ai.langchain4j;


import com.atguigu.java.ai.langchain4j.assistant.MemoryChatAssistant;
import com.atguigu.java.ai.langchain4j.assistant.SeparateChatAssistant;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
public class ChatMemoryTest {

    @Autowired
    private MemoryChatAssistant memoryChatAssistant;

    @Test
    public void testChatMemory() {
        String answer1 = memoryChatAssistant.chat("我是蔚然");
        System.out.println(answer1);

        String answer2 = memoryChatAssistant.chat("我是谁");
        System.out.println(answer2);
    }

    @Autowired
    private ChatLanguageModel chatLanguageModel;
    @Test
    public void testChatMemory2() {
        //第一轮对话
        UserMessage userMessage1 = UserMessage.userMessage("我的名字叫环环");
        ChatResponse chatResponse1 = chatLanguageModel.chat(userMessage1);
        AiMessage aiMessage1 = chatResponse1.aiMessage();
        //输出大语言模型的回复
        System.out.println(aiMessage1.text());

        //第二轮对话
        UserMessage userMessage2 = UserMessage.userMessage("你知道我的名字吗？");
//        ChatResponse chatResponse2 = chatLanguageModel.chat(Arrays.asList(userMessage1, userMessage2));
        ChatResponse chatResponse2 = chatLanguageModel.chat(Arrays.asList(userMessage1, aiMessage1, userMessage2));
        AiMessage aiMessage2 = chatResponse2.aiMessage();
        //输出大语言模型的回复
        System.out.println(aiMessage2.text());
    }

    @Test
    public void testChatMemory3() {
        //创建chatMemory 10 代表几个聊天记忆
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        //创建AIService
        MemoryChatAssistant memoryChatAssistant = AiServices
                .builder(MemoryChatAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(chatMemory)
                .build();
        //调用service的接口
        String answer1 = memoryChatAssistant.chat("我的名字是环环");
        System.out.println(answer1);
        String answer2 = memoryChatAssistant.chat("请问我的名字是？");
        System.out.println(answer2);
    }
    @Test
    public void testChatMemory4() {
        String answer1 = memoryChatAssistant.chat("我的名字是环环");
        System.out.println(answer1);
        String answer2 = memoryChatAssistant.chat("请问我的名字是？");
        System.out.println(answer2);
    }

    @Autowired
    private SeparateChatAssistant separateChatAssistant;

    @Test
    public void testChatMemory5() {
        String answer1 = separateChatAssistant.chat(1,"我的名字是环环");
        System.out.println(answer1);
        String answer2 = separateChatAssistant.chat(1,"我的名字是？");
        System.out.println(answer2);

        String answer3 = separateChatAssistant.chat(2,"我的名字是？");
        System.out.println(answer3);
    }
}
