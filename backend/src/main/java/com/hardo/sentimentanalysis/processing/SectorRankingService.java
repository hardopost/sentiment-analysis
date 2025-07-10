package com.hardo.sentimentanalysis.processing;

import com.hardo.sentimentanalysis.domain.Sector;
import com.hardo.sentimentanalysis.domain.SectorRepository;
import org.slf4j.Logger;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SectorRankingService {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(SectorRankingService.class);
    private final SectorRepository sectorRepository;
    private final ChatClient chatClient;
    @Value("classpath:prompts/sector-ranking-prompt.txt")
    private Resource sectorRankingPrompt;

    public SectorRankingService(SectorRepository sectorRepository, @Qualifier("geminiChatClient") ChatClient chatClient) {
        this.sectorRepository = sectorRepository;
        this.chatClient = chatClient;
    }


    public void rankSectors(String period, String market) {
        // Fetch all sectors from the database
        List<Sector> sectors = sectorRepository.findSummariesByPeriod(period, market);
        if (sectors.isEmpty()) {
            logger.info("No sectors found in the database.");
            return;
        } else {
            logger.info("Found {} sector summaries in the database.", sectors.size());
        }

        // Create the user input string from the sectors
        StringBuilder sb = new StringBuilder();
        for (Sector sector : sectors) {
            sb.append(StringInputBuilder.buildStringInputForSectorsRanking(sector)).append("\n");
        }
        String userInput = sb.toString();

        var systemMessage = new SystemMessage(sectorRankingPrompt); // contains rules, structure, tone guidance, etc.
        var userMessage = new UserMessage(userInput); // contains the statements in string format
        var prompt = new Prompt(List.of(systemMessage, userMessage)); // combines system and user messages

        logger.info("Sending prompt to chat client...");
        LlmResult<List<SectorRankingDTO>> llmResult = ChatClientUtil.sendPromptWithTokenInfo(
                chatClient,
                prompt,
                new ParameterizedTypeReference<>() {
                }
        );
        logger.info("Received response from chat client.");

        List<SectorRankingDTO> sectorRankings = llmResult.output();

        System.out.println("Sector Rankings:");
        for (SectorRankingDTO sectorRanking : sectorRankings) {
            System.out.printf("Sector: %s, Rank: %d, Rationale: %s%n",
                    sectorRanking.sector(), sectorRanking.rank(), sectorRanking.rationale());
        }

        for (SectorRankingDTO sectorRanking : sectorRankings) {
            Sector sector = sectorRepository.findSummaryBySectorAndMarket(sectorRanking.sector(), period, market);
            if (sector != null && sector.getSectorName().equals(sectorRanking.sector())) {
                sector.setRanking(sectorRanking.rank());
                sector.setRationale(sectorRanking.rationale());
                sector.setPromptTokens(sector.getPromptTokens() + llmResult.promptTokens());
                sector.setCompletionTokens(sector.getCompletionTokens() + llmResult.completionTokens());
                sector.setTotalTokens(sector.getTotalTokens() + llmResult.totalTokens());
                sectorRepository.save(sector);
                logger.info("Updated sector {} with rank {} and rationale: {}", sector.getSectorName(), sectorRanking.rank(), sectorRanking.rationale());
            } else {
                logger.warn("Sector {} not found in the database.", sectorRanking.sector());
            }

        }

    }
}
