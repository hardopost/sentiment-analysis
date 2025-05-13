package com.hardo.sentimentanalysis.processing;

import com.hardo.sentimentanalysis.domain.*;
import org.slf4j.Logger;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CompanyRankingService {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(CompanyRankingService.class);
    private final SectorRepository sectorRepository;
    private final ReportRepository reportRepository;
    private final ChatClient chatClient;
    @Value("classpath:prompts/company-ranking-prompt.txt")
    private Resource companyRankingPrompt;

    public CompanyRankingService(SectorRepository sectorRepository, ReportRepository reportRepository, ChatClient.Builder builder) {
        this.sectorRepository = sectorRepository;
        this.reportRepository = reportRepository;
        this.chatClient = builder.defaultOptions(ChatOptions.builder().temperature(0.2d).build()).build();
    }


    public void rankCompaniesInSectors(String period) {
        List<Sector> sectors = sectorRepository.findSummariesByPeriod(period);

        if (sectors.isEmpty()) {
            logger.info("No sectors found in the database.");
            return;
        } else {
            logger.info("Found {} sector summaries in the database.", sectors.size());
        }

        for (Sector sector : sectors) {
            List<Report> reports = reportRepository.findReportsBySectorAndPeriod(sector.getSectorName(), period);

            // Create the user input string from the sectors
            StringBuilder sb = new StringBuilder();
            sb.append("**").append(sector.getSectorName()).append(" SECTOR SUMMARY**\n");
            sb.append(StringInputBuilder.buildStringInputForSectorsRanking(sector)).append("\n");
            sb.append("**COMPANY REPORTS**\n");
            for (Report report : reports) {
                sb.append(StringInputBuilder.buildStringInputForCompanyRanking(report)).append("\n");
            }
            String userInput = sb.toString();
            //System.out.println(userInput);

            var systemMessage = new SystemMessage(companyRankingPrompt); // contains rules, structure, tone guidance, etc.
            var userMessage = new UserMessage(userInput); // contains the statements in string format
            var prompt = new Prompt(List.of(systemMessage, userMessage)); // combines system and user messages

            logger.info("Sending prompt to chat client...");
            LlmResult<List<CompanyRankingDTO>> llmResult = ChatClientUtil.sendPromptWithTokenInfo(
                chatClient,
                prompt,
                new ParameterizedTypeReference<>() {
                }
            );
            logger.info("Received response from chat client.");

            List<CompanyRankingDTO> companyRankings = llmResult.output();


             for (CompanyRankingDTO companyRanking : companyRankings) {
                Optional<Report> report = reportRepository.findById(companyRanking.companyId());
                  if (report.isPresent()) {
                      Report existingReport = getExistingReport(companyRanking, report, llmResult);
                      reportRepository.save(existingReport);
                      logger.info("Updated report for company: {} with rank: {}", existingReport.getCompanyName(), companyRanking.rank());
                  } else {
                      logger.error("Report not found for company ID: {}", companyRanking.companyId());
                    }
                }
            }



    }

    private static Report getExistingReport(CompanyRankingDTO companyRanking, Optional<Report> report, LlmResult<List<CompanyRankingDTO>> llmResult) {
        Report existingReport = report.get();
        existingReport.setRank(companyRanking.rank());
        existingReport.setRationale(companyRanking.rationale());
        existingReport.setPromptTokens(existingReport.getPromptTokens() + llmResult.promptTokens());
        existingReport.setCompletionTokens(existingReport.getCompletionTokens() + llmResult.completionTokens());
        existingReport.setTotalTokens(existingReport.getTotalTokens() + llmResult.totalTokens());
        return existingReport;
    }


}
