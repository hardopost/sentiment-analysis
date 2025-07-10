package com.hardo.sentimentanalysis.processing;

import com.hardo.sentimentanalysis.domain.*;
import com.hardo.sentimentanalysis.extraction.PDFExtractorService;
import org.slf4j.Logger;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class StatementValidationService {


    private final Logger logger = org.slf4j.LoggerFactory.getLogger(StatementValidationService.class);
    private final PDFExtractorService pdfExtractorService;
    private final StatementRepository statementRepository;
    private final StatementValidationRepository statementValidationRepository;
    private final ChatClient chatClient;
    private final ReportRepository reportRepository;
    @Value("classpath:prompts/validation-prompt.txt")
    private Resource validationPrompt;


    public StatementValidationService(StatementRepository statementRepository, PDFExtractorService pdfExtractorService, StatementValidationRepository statementValidationRepository, @Qualifier("geminiChatClient") ChatClient chatClient, ReportRepository reportRepository) {
        this.pdfExtractorService = pdfExtractorService;
        this.statementRepository = statementRepository;
        this.statementValidationRepository = statementValidationRepository;
        this.chatClient = chatClient;
        this.reportRepository = reportRepository;
    }

    private Long extractReportIdFromFileName(String filename) {
        try {
            return Long.parseLong(filename.split("_")[0]);
        } catch (Exception e) {
            return null; // or throw if you want to enforce this
        }
    }

    // 10 random report id-s: 1 5 103 122 156 162 206 244 322 365
    public void validateStatements(File pdfFile) throws IOException {

        String reportName = pdfFile.getName();
        Long reportId = extractReportIdFromFileName(reportName);
        Report report = reportRepository.findReportById(reportId);

        List<Statement> statements = statementRepository.findByReportId(reportId); // Fetch statements for the report

        // Create the user input string from the statements
        StringBuilder sb = new StringBuilder();
        sb.append("**STATEMENTS TO VALIDATE BEGIN, BELOW ARE STATEMENTS TO VALIDATE:**").append("\n").append("\n"); // Add a prompt to validate the statements
        for (Statement s : statements) {
            sb.append(StringInputBuilder.buildStringInputValidationStatement(s)).append("\n"); // Method to build string input for each sector statement
        }
        //String userInput = sb.toString(); //All statements inputs in one string

        sb.append("**STATEMENTS ENDED, BELOW IS REPORT TEXT FOR VALIDATION**").append("\n").append("\n"); // Add a prompt to validate the statements

        String reportText = pdfExtractorService.extractTextFromPDF(pdfFile);

        sb.append(reportText).append("\n"); // Add the text from the report

        String userInput = sb.toString(); // All statements inputs in one string

        //System.out.println("User input: " + userInput);




        var systemMessage = new SystemMessage(validationPrompt);
        var userMessage = new UserMessage(userInput);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));


        LlmResult<List<StatementValidationDTO>> llmResult = ChatClientUtil.sendPromptWithTokenInfo(
                chatClient,
                prompt,
                new ParameterizedTypeReference<>() {
                }
        );

        System.out.println("Prompt tokens: " + llmResult.promptTokens());
        System.out.println("Completion tokens: " + llmResult.completionTokens());
        System.out.println("Total tokens: " + llmResult.totalTokens());
        System.out.println("Statement count: " + statements.size());
        System.out.println("LLM result: " + llmResult.output().size());

        List<StatementValidationDTO> validationResults = llmResult.output();

        for (StatementValidationDTO validationResult : validationResults) {
            StatementValidation sv = new StatementValidation(
                    null,
                    reportId,
                    validationResult.givenStatement(),
                    validationResult.foundStatement(),
                    validationResult.matchFound(),
                    validationResult.isTypeValid(),
                    validationResult.isCategoryValid(),
                    validationResult.isSentimentValid(),
                    validationResult.isSectorValid(),
                    validationResult.comment(),
                    null,
                    validationResult.esg()
            );
            statementValidationRepository.save(sv);
        }
        report.setValidationPromptTokens(llmResult.promptTokens());
        report.setValidationCompletionTokens(llmResult.completionTokens());
        report.setValidationTotalTokens(llmResult.totalTokens());
        reportRepository.save(report);

        System.out.println("Validation results saved to database.");

    }
}
