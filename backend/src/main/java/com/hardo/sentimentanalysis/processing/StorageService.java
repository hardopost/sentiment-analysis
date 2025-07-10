package com.hardo.sentimentanalysis.processing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hardo.sentimentanalysis.domain.Report;
import com.hardo.sentimentanalysis.domain.Statement;
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
    public void storeStatementsWithEmbeddings(List<Statement> statements, List<Embedding> embeddings) {

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
                            id, market, type, company_name, sector, category,
                            content, sentiment, period, capitalization,
                            embedding, metadata, report_id
                        ) VALUES (
                            :id, :market, :type, :companyName, :sector, :category,
                            :content, :sentiment, :period, :capitalization,
                            :embedding::vector, :metadata::jsonb, :reportId
                        )
                        """)
                            .param("id", statement.getId())
                            .param("market", statement.getMarket())
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
                    "market", statement.getMarket(),
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

    private String buildMetadataJsonForReport(Report report, CompanySummaryDTO summaryDTO) {
        try {
            Map<String, Object> metadata = Map.of(
                    "market", report.getMarket(),
                    "companyName", report.getCompanyName(),
                    "period", report.getPeriod(),
                    "sector", report.getSector(),
                    "summary", summaryDTO.summary(),
                    "tone", summaryDTO.tone(),
                    "positiveDrivers", summaryDTO.positiveDrivers(),
                    "negativeDrivers", summaryDTO.negativeDrivers(),
                    "capitalization", report.getCapitalization()
            );
            return objectMapper.writeValueAsString(metadata);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize metadata for report: " + report.getId(), e);
        }
    }

    @Transactional
    public void updateReportAfterProcessing(Report report, LlmResult<StatementExtractionResponse> llmResult, String embeddingString, long statementProcessingTime, long embeddingProcessingTime, int embeddingTotalTokens) {
        try {
            String metadataJson = buildMetadataJsonForReport(report, llmResult.output().summary());

            jdbcClient.sql("""
            UPDATE report SET
                report_type = :reportType,
                report_title_full = :reportTitleFull,
                report_fiscal_year = :reportFiscalYear,
                metadata = :metadata::jsonb,
                embedding = :embedding::vector,
                statement_prompt_tokens = :statementPromptTokens,
                statement_completion_tokens = :statementCompletionTokens,
                statement_total_tokens = :statementTotalTokens,
                llm_time_ms = :llmTimeMs,
                embedding_time_ms = :embeddingTimeMs,
                processed = true,
                embedding_total_tokens = :embeddingTotalTokens,
                content = :content
                  
            WHERE id = :reportId
        """)
                    .param("reportType", llmResult.output().summary().reportType())
                    .param("reportTitleFull", llmResult.output().summary().reportTitleFull())
                    .param("reportFiscalYear", llmResult.output().summary().reportFiscalYear())
                    .param("metadata", metadataJson)
                    .param("embedding", embeddingString)
                    .param("statementPromptTokens", llmResult.promptTokens())
                    .param("statementCompletionTokens", llmResult.completionTokens())
                    .param("statementTotalTokens", llmResult.totalTokens())
                    .param("llmTimeMs", statementProcessingTime)
                    .param("embeddingTimeMs", embeddingProcessingTime)
                    .param("reportId", report.getId())
                    .param("embeddingTotalTokens", embeddingTotalTokens)
                    .param("content", llmResult.output().summary().toString())
                    .update();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update report after processing", e);
        }
    }
}
