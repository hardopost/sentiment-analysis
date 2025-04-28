package com.hardo.sentimentanalysis.processing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hardo.sentimentanalysis.util.LlmResult;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.IntStream;

@Service
public class StorageService {

    private final JdbcClient jdbcClient;
    private final ObjectMapper objectMapper;

    public StorageService(JdbcClient jdbcClient, ObjectMapper objectMapper) {
        this.jdbcClient = jdbcClient;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void storeStatementsWithEmbeddings(List<Statement> statements, EmbeddingResponse embeddingResponse) {
        List<Embedding> embeddings = embeddingResponse.getResults();

        if (statements.size() != embeddings.size()) {
            throw new IllegalArgumentException("Statements and embeddings size must match.");
        }

        IntStream.range(0, statements.size())
                .forEach(i -> {

                    Statement statement = statements.get(i);
                    Embedding embedding = embeddings.get(i);

                    String embeddingString = Arrays.toString(embedding.getOutput()); // Convert List<Float> -> String
                    String metadataJson = buildMetadataJson(statement);

                    jdbcClient.sql("""
                        INSERT INTO statement (
                            id, type, company_name, sector, category,
                            content, sentiment, period, capitalization,
                            embedding, metadata, report_id
                        ) VALUES (
                            :id, :type, :companyName, :sector, :category,
                            :content, :sentiment, :period, :capitalization,
                            :embedding::vector, :metadata::jsonb, :reportId
                        )
                        """)
                            .param("id", statement.getId())
                            .param("type", statement.getType().toString())
                            .param("companyName", statement.getCompanyName())
                            .param("sector", statement.getSector())
                            .param("category", statement.getCategory())
                            .param("content", statement.getContent())
                            .param("sentiment", statement.getSentiment())
                            .param("period", statement.getPeriod())
                            .param("capitalization", statement.getCapitalization())
                            .param("embedding", embeddingString)
                            .param("metadata", metadataJson)
                            .param("reportId", statement.getReportId())
                            .update();
                });
    }

    private String buildMetadataJson(Statement statement) {
        try {
            Map<String, Object> metadata = Map.of(
                    "companyName", statement.getCompanyName(),
                    "period", statement.getPeriod(),
                    "type", statement.getType().toString(),
                    "sector", statement.getSector(),
                    "category", statement.getCategory(),
                    "sentiment", statement.getSentiment(),
                    "capitalization", statement.getCapitalization()
            );
            return objectMapper.writeValueAsString(metadata);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize metadata for statement: " + statement.getId(), e);
        }
    }

    @Transactional
    public void updateReportAfterProcessing(Report report, LlmResult<StatementExtractionResponse> llmResult, long statementProcessingTime, long embeddingProcessingTime) {
        try {
            String outlookSummaryJson = objectMapper.writeValueAsString(llmResult.output().summary());

            jdbcClient.sql("""
            UPDATE report SET
                report_type = :reportType,
                outlook_summary = :outlookSummary::jsonb,
                prompt_tokens = :promptTokens,
                completion_tokens = :completionTokens,
                total_tokens = :totalTokens,
                llm_time_ms = :llmTimeMs,
                embedding_time_ms = :embeddingTimeMs,
                processed = true
            WHERE id = :reportId
        """)
                    .param("reportType", llmResult.output().summary().reportType())
                    .param("outlookSummary", outlookSummaryJson)
                    .param("promptTokens", llmResult.promptTokens())
                    .param("completionTokens", llmResult.completionTokens())
                    .param("totalTokens", llmResult.totalTokens())
                    .param("llmTimeMs", statementProcessingTime)
                    .param("embeddingTimeMs", embeddingProcessingTime)
                    .param("reportId", report.getId())
                    .update();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update report after processing", e);
        }
    }
}
