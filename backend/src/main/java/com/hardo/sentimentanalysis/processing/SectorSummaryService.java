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

@Component
public class SectorSummaryService {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(SectorSummaryService.class);
    private final SectorRepository sectorRepository;
    private final StatementRepository statementRepository;
    private final ChatClient chatClient;
    @Value("classpath:prompts/sector-summary-prompt.txt")
    private Resource reportSummaryPrompt;


    public SectorSummaryService(SectorRepository sectorRepository, StatementRepository statementRepository, ChatClient.Builder builder) {
        this.sectorRepository = sectorRepository;
        this.statementRepository = statementRepository;
        this.chatClient = builder.defaultOptions(ChatOptions.builder().temperature(0.0d).build()).build();
    }

    public void generateSectorSummary(String period) {

        List<String> sectors = List.of(
                "Technology", "Telecommunications", "Health Care", "Financials", "Real Estate",
                "Consumer Discretionary", "Consumer Staples", "Industrials", "Basic Materials",
                "Energy", "Utilities"
        );

        for (String sector : sectors) {

            logger.info("Processing sector: {}", sector);
            List<Statement> statements = statementRepository.findByTypeAndSectorOrderByCategory(Statement.Type.sector, sector, period); // Fetch statements for the sector
            if (statements.isEmpty()) {
                logger.info("No statements found from database for the {} sector.", sector);
                return;
            }
            else {
                logger.info("Found {} statements for the {} sector: ", statements.size(), sector);
            }

            // Create the user input string from the statements
            StringBuilder sb = new StringBuilder();
            for (Statement s : statements) {
                sb.append(StringInputBuilder.buildStringInputForSectorStatement(s)); //Method to build string input for each sector statement
            }
            String userInput = sb.toString(); //All sector statement inputs in one string


            var systemMessage = new SystemMessage(reportSummaryPrompt); // contains rules, structure, tone guidance, etc.
            var userMessage = new UserMessage(userInput); // contains the statements in string format
            var prompt = new Prompt(List.of(systemMessage, userMessage)); // combines system and user messages

            logger.info("Sending prompt to chat client...");
            LlmResult<SectorSummaryDTO> llmResult = ChatClientUtil.sendPromptWithTokenInfo(
                    chatClient,
                    prompt,
                    new ParameterizedTypeReference<>() {
                    }
            );
            logger.info("Received response from chat client.");

            Sector sectorEntity = new Sector(
                    null,
                    sector,
                    llmResult.output().summary(),
                    llmResult.output().sentimentJustification(),
                    llmResult.output().tone(),
                    period,
                    llmResult.promptTokens(),
                    llmResult.completionTokens(),
                    llmResult.totalTokens(),
                    null,
                    null,
                    null);

            System.out.println("Prompt Tokens: " + llmResult.promptTokens());
            System.out.println("Completion Tokens: " + llmResult.completionTokens());
            System.out.println("Total Tokens: " + llmResult.totalTokens());
            System.out.println("-----------------------------------------------------------------------------------------------------------");
            System.out.println("Tone: " + llmResult.output().tone());
            System.out.println("Summary: " + llmResult.output().summary());
            System.out.println("Justification: " + llmResult.output().sentimentJustification());

            // Save the sector summary to the database
            sectorRepository.save(sectorEntity);
            logger.info("Sector summary saved to database for the {} sector.", sector);
        }

    }

}
