package com.hardo.sentimentanalysis.config;

import com.hardo.sentimentanalysis.chat.StatementContextAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatConfig {

    @Bean
    public StatementContextAdvisor advisor(VectorStore vectorStore) {
        return new StatementContextAdvisor(vectorStore);
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, StatementContextAdvisor advisor) {
        return builder
                .defaultAdvisors(List.of(advisor))
                .build();
    }

}
