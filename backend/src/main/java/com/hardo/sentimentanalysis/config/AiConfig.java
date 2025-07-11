package com.hardo.sentimentanalysis.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;


@Configuration
public class AiConfig {

    @Bean ChatClient openAiChatClient(OpenAiChatModel chatModel, VectorStore vectorStore) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(20).build(); // In memory for the chat client
        MessageChatMemoryAdvisor chatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        //Create a QuestionAnswerAdvisor, without configuring it searches all documents in the vector store
        QuestionAnswerAdvisor qaAdvisor = QuestionAnswerAdvisor
                .builder(vectorStore) // using already configured vector store in VectorStoreConfig
                .searchRequest(SearchRequest.builder().topK(20).build())
                .build();

        return ChatClient.builder(chatModel)
                .defaultAdvisors(chatMemoryAdvisor, qaAdvisor) // Add the chat memory advisor
                .build();
    }

    @Bean ChatClient geminiChatClient(VertexAiGeminiChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultOptions(ChatOptions.builder().temperature(0.0d).build())
                .build();
    }

    @Bean
    EmbeddingModel embeddingModel(OpenAiEmbeddingModel embeddingModel) {
        return embeddingModel;
    }

    @Bean
    public VectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .dimensions(1536)
                .distanceType(PgVectorStore.PgDistanceType.EUCLIDEAN_DISTANCE)
                .indexType(PgVectorStore.PgIndexType.HNSW)
                .initializeSchema(false)
                .schemaName("public")
                .vectorTableName("report")
                .maxDocumentBatchSize(10000)
                .build();
    }

}
