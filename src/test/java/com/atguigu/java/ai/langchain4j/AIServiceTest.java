package com.atguigu.java.ai.langchain4j;

import com.atguigu.java.ai.langchain4j.assistant.MemoryChatAssistant;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AIServiceTest {

    //    @Autowired
//    private QwenChatModel qwenChatModel;
    @Autowired
    private ChatLanguageModel chatLanguageModel;

    @Test
    public void testChat() {
        //创建AIService
        MemoryChatAssistant memoryChatAssistant = AiServices.create(MemoryChatAssistant.class, chatLanguageModel);
        //调用service的接口
        String answer = memoryChatAssistant.chat("Hello");
        System.out.println(answer);
    }

    @Autowired
    private MemoryChatAssistant memoryChatAssistant;
    @Test
    public void testAi() {
        String answer = memoryChatAssistant.chat("Hello");
        System.out.println(answer);
    }
}
