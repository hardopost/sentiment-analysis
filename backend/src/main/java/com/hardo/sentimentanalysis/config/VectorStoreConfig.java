package com.hardo.sentimentanalysis.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class VectorStoreConfig {


    @Bean
    public VectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .dimensions(768) // Vertex AI embedding model's 'text-embedding-005' dimensions are max 768
                .distanceType(PgVectorStore.PgDistanceType.EUCLIDEAN_DISTANCE) //try EUCLIDEAN_DISTANCE or NEGATIVE_INNER_PRODUCT, if vector are normalized better according to documentation
                .indexType(PgVectorStore.PgIndexType.HNSW) // HNSW is the default index type
                .initializeSchema(false) // Set to true if you want to create the schema automatically if it doesn't exist
                .schemaName("public") // Optional: defaults to "public", but can change here
                .vectorTableName("report")
                .maxDocumentBatchSize(10000) // Optional: defaults to 10000
                .build();
    }

}
