package com.atguigu.java.ai.langchain4j;

import com.atguigu.java.ai.langchain4j.assistant.SeparateChatAssistant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PromptTest {

    @Autowired
    private SeparateChatAssistant separateChatAssistant;

    @Test
    public void testSystemMessage() {
        String answer = separateChatAssistant.chat(5,"今天几号？");
        System.out.println(answer);
    }
    @Test
    public void testV() {
        String answer1 = separateChatAssistant.chat2(6, "我是环环");
        System.out.println(answer1);
        String answer2 = separateChatAssistant.chat2(6, "我是谁");
        System.out.println(answer2);
    }
    @Test
    public void testUserInfo() {
        String answer = separateChatAssistant.chat3(7, "我是谁，我多大了", "蔚然", 18);
        System.out.println(answer);
    }

}