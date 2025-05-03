package com.atguigu.java.ai.langchain4j.assistant;

import dev.langchain4j.service.*;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(
        wiringMode = EXPLICIT,
//        chatModel = "ollamaChatModel",
        streamingChatModel = "qwenStreamingChatModel",
//        streamingChatModel = "ollamaStreamingChatModel",
        chatMemoryProvider = "chatMemoryProviderXiaozhi",
        tools = "appointmentTools",
        contentRetriever = "contentRetrieverXiaozhiPincone"

)
public interface XiaozhiAgent {

    @SystemMessage(fromResource = "zhaozhi-prompt-template.txt")
    Flux<String> chat(@MemoryId Long memoryId, @UserMessage String userMessage);
}
