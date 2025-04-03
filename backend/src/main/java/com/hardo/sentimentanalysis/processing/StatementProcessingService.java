package com.hardo.sentimentanalysis.processing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hardo.sentimentanalysis.extraction.PDFExtractorService;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class StatementProcessingService {

    private final PDFExtractorService pdfExtractorService;
    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final JdbcClient jdbcClient;
    private final ObjectMapper objectMapper;
    @Value("classpath:prompts/extract-prompt.txt")
    private Resource extractPrompt;


    public StatementProcessingService(PDFExtractorService pdfExtractorService, ChatClient.Builder builder, StatementRepository statementRepository, EmbeddingModel embeddingModel, JdbcClient jdbcClient, ObjectMapper objectMapper) {
        this.pdfExtractorService = pdfExtractorService;
        this.chatClient = builder.defaultOptions(ChatOptions.builder().temperature(0.0d).build()).build();
        this.embeddingModel = embeddingModel;
        this.jdbcClient = jdbcClient;
        this.objectMapper = objectMapper;
    }

    public void processPdf(String pdfName) throws IOException {
        String extractedText = pdfExtractorService.extractTextFromPDF();
        System.out.println(extractedText);


        var userMessage = new UserMessage(
        "The growth continued in 2024 with an order intake of 74.6 BSEK, corresponding to an organic growth of 7 percent. The momentum continued strong in the fourth quarter, ending the year somewhat above expectations.\n" +
                "The Marine Division led the growth, ending the year at almost 30 BSEK, 24 percent above 2023 with growth across the entire product portfolio. The ship contracting market is expected to remain firm, although the exceptional conditions in the tanker market in 2023-2024 will likely not repeat in 2025.The Energy Division compensated for the decline in the HVAC market, including heat pumps. Excluding HVAC, the division grew with 6 percent. The growth was supported by new applications in clean tech, which grew with 40 percent compared to 2023. Despite global concerns regarding the speed of the energy transition the project pipeline continues to grow at a healthy rate. The Food & Water Division had an exceptional year in 2023 driven by a record order intake in Desmet with several large project orders. Consequently, order intake declined somewhat in 2024, as expected. Still, there was a healthy growth in most product and application areas, including the important channel partner business and excluding Desmet, the division grew by 6 percent.\n" +
                "The EBITA margin for the full year improved somewhat compared to last year and ended at 16.6 percent. The fourth quarter also improved slightly to 16 percent. The profitability in the quarter is affected by a seasonally higher share of project invoicing at year end. In addition, costs for a few restructuring projects in the Food & Water Division and the Marine Division had a negative cost impact of about 200 MSEK in the quarter. The charges are considered as normal costs of running and adapting the business to an evolving market, and not as comparison distortion items.\n" +
                "The market positions have been strengthened in most end markets, supported by continued new product launches and capacity investments during the year. The accelerated growth of the service business continued in 2024 with 8 percent organic growth. The strategic focus and investments into the service organization will support the growth in the years to come.\n" +
                "After a year with a record operating cash flow of 12.2 BSEK, the group enters 2025 with a strong balance sheet to support the growth agenda. The strong order book of 52 BSEK provides a strong invoicing platform for the year. Despite considerable macroeconomic uncertainties, market conditions are expected to remain favourable in most of Alfa Laval´s end markets. In the short-term, market demand is expected to remain on about the same level as in the fourth quarter of 2024.");

        var systemMessage = new SystemMessage(extractPrompt);

        List<StatementDTO> statements = chatClient.prompt(new Prompt(List.of(systemMessage, userMessage)))
                .call()
                .entity(new ParameterizedTypeReference<List<StatementDTO>>() {
                });


        List<Statement> statementEntities = statements.stream()
                .map(StatementMapper::fromDTO)
                .toList();

        for (Statement statement : statementEntities) {
            String enrichedStatement = EmbeddingInputBuilder.buildEmbeddingInput(statement); // vectorize statement and other info together
            float[] embedding = embeddingModel.embed(enrichedStatement);  // embed one statement at a time
            String embeddingString = Arrays.toString(embedding);

            Map<String, Object> metadata = Map.of(
                    "type", statement.getType().toString(),
                    "companyName", statement.getCompanyName(),
                    "industry", statement.getIndustry(),
                    "supersector", statement.getSupersector(),
                    "sector", statement.getSector(),
                    "category", statement.getCategory(),
                    "sentiment", statement.getSentiment(),
                    "period", statement.getPeriod()
            );

            String metadataJson = objectMapper.writeValueAsString(metadata);


            jdbcClient.sql("""
                            INSERT INTO statement (
                            id, type, company_name, industry, supersector, sector, category,
                            content, sentiment, period, embedding, metadata)
                            VALUES (:id, :type, :companyName, :industry, :supersector, :sector, :category,
                            :content, :sentiment, :period, :embedding::vector, :metadata::jsonb)""")
                    .param("id", statement.getId())
                    .param("type", statement.getType().toString())
                    .param("companyName", statement.getCompanyName())
                    .param("industry", statement.getIndustry())
                    .param("supersector", statement.getSupersector())
                    .param("sector", statement.getSector())
                    .param("category", statement.getCategory())
                    .param("content", statement.getContent())
                    .param("sentiment", statement.getSentiment())
                    .param("period", statement.getPeriod())
                    .param("embedding", embeddingString)
                    .param("metadata", metadataJson)
                    .update();
        }


    }

}

