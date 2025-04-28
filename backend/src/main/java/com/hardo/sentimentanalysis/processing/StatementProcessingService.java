package com.hardo.sentimentanalysis.processing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hardo.sentimentanalysis.extraction.PDFExtractorService;


import com.hardo.sentimentanalysis.util.ChatClientUtil;
import com.hardo.sentimentanalysis.util.LlmResult;
import org.slf4j.Logger;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Component
public class StatementProcessingService {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(StatementProcessingService.class);
    private final PDFExtractorService pdfExtractorService;
    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final ObjectMapper objectMapper;
    private final ReportRepository reportRepository;
    private final StorageService storageService;
    @Value("classpath:prompts/extract-prompt.txt")
    private Resource extractPrompt;


    public StatementProcessingService(PDFExtractorService pdfExtractorService, ChatClient.Builder builder, StatementRepository statementRepository, EmbeddingModel embeddingModel , ObjectMapper objectMapper, ReportRepository reportRepository, StorageService storageService) {
        this.pdfExtractorService = pdfExtractorService;
        this.chatClient = builder.defaultOptions(ChatOptions.builder().temperature(0.0d).build()).build();
        this.embeddingModel = embeddingModel;
        this.objectMapper = objectMapper;
        this.reportRepository = reportRepository;
        this.storageService = storageService;
    }

    private Long extractReportIdFromFileName(String filename) {
        try {
            return Long.parseLong(filename.split("_")[0]);
        } catch (Exception e) {
            return null; // or throw if you want to enforce this
        }
    }

    private Optional<Report> getUnprocessedReport(Long reportId, String reportName) {
        if (reportId == null) {
            System.out.println("⚠️ Skipping: Cannot extract report ID from " + reportName);
            return Optional.empty();
        }

        return reportRepository.findById(reportId)
                .filter(report -> {
                    if (report.isProcessed()) {
                        System.out.println("⏭️ Skipping already processed report: " + report.getCompanyName());
                        return false; // skip if processed
                    }
                    return true; // continue if unprocessed
                });
    }

    public static int estimateTokenCount(String text) {
        return text.length() / 4;
    }

    public void processPdf(File pdfFile) throws IOException {

        String reportName = pdfFile.getName();
        Long reportId = extractReportIdFromFileName(reportName);

        Optional<Report> optionalReport = getUnprocessedReport(reportId, reportName);
        if (optionalReport.isEmpty()) {
            return; // Skip processing if report not found or already processed
        }

        Report report = optionalReport.get(); // Continue processing

        String reportText = pdfExtractorService.extractTextFromPDF(pdfFile);

        String estimatedTokens = String.valueOf(estimateTokenCount(reportText));
        System.out.println("Estimated tokens: " + estimatedTokens);


        var systemMessage = new SystemMessage(extractPrompt);
        var userMessage = new UserMessage(reportText);

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        long statementProcessingStartTime = System.currentTimeMillis();
        LlmResult<StatementExtractionResponse> llmResult = ChatClientUtil.sendPromptWithTokenInfo(
                chatClient,
                prompt,
                new ParameterizedTypeReference<>() {
                }
        );
        long statementProcessingEndTime = System.currentTimeMillis();
        long statementProcessingTimeMs = statementProcessingEndTime - statementProcessingStartTime;

        logger.info("Extracted {} statements from text.", llmResult.output().statements().size());

        List<Statement> statementEntities = llmResult.output().statements().stream()
                .map(dto -> StatementMapper.fromReportAndStatementDTO(dto, report))
                .toList();

        List<String> enrichedStatements = statementEntities.stream()
                .map(EmbeddingInputBuilder::buildEmbeddingInput)  // statement -> EmbeddingInputBuilder.buildEmbeddingInput(statement)
                .toList();                                        // "[Company: Alfa Laval] [Category: Revenue Growth Expectations] ... We expect revenue to grow by 10% next year."

        for (String enrichedStatement : enrichedStatements) {
            System.out.println("Enriched statement: " + enrichedStatement);
        }

        // Send statements to embedding model and get List<Embedding> embeddings and Metadata
        long embeddingStartTime = System.currentTimeMillis();
        EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(enrichedStatements);
        long embeddingEndTime = System.currentTimeMillis();
        long embeddingProcessingTimeMs = embeddingEndTime - embeddingStartTime;
        logger.info("Created {} embeddings from statements.", embeddingResponse.getResults().size());
        logger.info("Embedding metadata: {}", embeddingResponse.getMetadata());

        //With experimental embedding model need to send Strings one by one
        /*List<Embedding> embeddings = new ArrayList<>();
        long embeddingStartTime = System.currentTimeMillis();
        for (String enrichedStatement : enrichedStatements) {
            Embedding embedding = embeddingModel.embedForResponse(List.of(enrichedStatement)).getResult();
            logger.info("Embedding: {}", Arrays.toString(embedding.getOutput()));
            embeddings.add(embedding);
        }
        logger.info("Created {} embeddings from statements.", embeddings.size());*/

        // Store Statement and Embeddings to database
        storageService.storeStatementsWithEmbeddings(statementEntities, embeddingResponse); //embeddingResponse



        System.out.println(llmResult.output().summary().reportType());
        System.out.println(llmResult.output().summary().summary());
        System.out.println(llmResult.output().summary().tone());
        System.out.println(llmResult.output().summary().positiveDrivers().toString());
        System.out.println(llmResult.output().summary().negativeDrivers().toString());
        System.out.println("LLM time: " + statementProcessingTimeMs);
        System.out.println("Embedding time: " + embeddingProcessingTimeMs);

        storageService.updateReportAfterProcessing(report, llmResult, statementProcessingTimeMs, embeddingProcessingTimeMs);
    }

}

