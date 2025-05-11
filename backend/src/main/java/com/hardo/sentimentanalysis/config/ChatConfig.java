package com.hardo.sentimentanalysis.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ChatConfig {

    public final VectorStore vectorStore;

    public ChatConfig(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(20).build(); // In memory for the chat client
        MessageChatMemoryAdvisor chatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        //Create a QuestionAnswerAdvisor, without configuring it searches all documents in the vector store
        QuestionAnswerAdvisor qaAdvisor = QuestionAnswerAdvisor
                .builder(vectorStore) // using already configured vector store in VectorStoreConfig
                .searchRequest(SearchRequest.builder().topK(6).build())
                .build();

        return builder
                .defaultAdvisors(chatMemoryAdvisor, qaAdvisor) // Add the advisor to the default advisors
                .build();
    }
}
