package com.hardo.sentimentanalysis.search;

import com.hardo.sentimentanalysis.domain.Report;
import com.hardo.sentimentanalysis.domain.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ReportLinkDiscoveryService {

    private final GoogleSearchService searchService;
    private final ReportRepository reportRepository;
    private final ChatClient chatClient;
    private final Logger log = LoggerFactory.getLogger(ReportLinkDiscoveryService.class);

    public ReportLinkDiscoveryService(GoogleSearchService searchService, ReportRepository reportRepository, ChatClient chatClient) {
        this.searchService = searchService;
        this.reportRepository = reportRepository;
        this.chatClient = chatClient;
    }

    public void discoverAndSaveLinks() {
        List<Report> reports = reportRepository.findAllById(List.of(13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L)); // Adjust page size as needed

        for (Report report : reports) {
            if (report.getDownloadLink() != null) continue; // Skip already resolved ones


            /*String query = String.format(
                    "after:2025-01-01 \"%s\" (\"annual report 2024\" OR \"year-end report 2024\") filetype:pdf (download OR -download)",
                    report.getCompanyName()
            );*/

            String query = String.format(
                    "site:mb.cision.com %s 2024 annual report filetype:pdf",
                    report.getCompanyName()
            );

            String fallbackQuery = String.format(
                    "site:storage.mfn.se %s 2024 annual report filetype:pdf",
                    report.getCompanyName()
            );


            String bestLink = tryGetLink(report.getCompanyName(), query);

            if (bestLink == null) {
                System.out.println("❌ No link found for: " + report.getCompanyName());
                System.out.println("🔁 Retrying alternative search for: " + report.getCompanyName());
                bestLink = tryGetLink(report.getCompanyName(), fallbackQuery);
            }

            if (bestLink != null) {
                report.setDownloadLink(bestLink);
                reportRepository.save(report);
                System.out.println("✅ Saved link for: " + report.getCompanyName());
                log.info("Best link for {}: {}", report.getCompanyName(), bestLink);
            }
        }
    }

    private String tryGetLink(String companyName, String query) {
        List<Map<String, String>> searchResults = searchService.search(query);
        String response = askLLMToChooseLink(companyName, searchResults);

        return response == null ? null : extractLinkFrom(response);
    }

    private String askLLMToChooseLink(String companyName, List<Map<String, String>> results) {
        /*var systemMessage = new SystemMessage("You will receive results of a Google search for the 2024 Annual report of one OMX Nasdaq Stockholm main list company.\n" +
                "Your task is to choose the PDF file download link for the report that is one of the 4 types listed below. Prefer report that is higher in the list, if" +
                "the report type higher in the list is not available, return the download link to type of report that is lower in the list: " +
                "1. 2024 annual report, " +
                "2. 2024 year-end and q4 report," +
                "3. 2024 interim q4 report," +
                "4. 2024 annual and sustainability report" +
                "Avoid links to presentations. Prefer the report that is in English language, if report is not available in English, report in Swedish language is also suitable. Reply with only the link." +
                "However if there are no candidates for reports in the list above, reply with 'No good candidate'.");*/
        StringBuilder m1 = new StringBuilder("Below are the results of a Google search for the 2024 Annual report of OMX Nasdaq Stockholm main list company ").append(companyName).append(".\n");
        String m2 = """
                Your task is to extract the PDF file download link of the company's 2024 Annual Report or 2024 Annual and Sustainability Report
                - do not extract link to presentation
                - prefer the report that is in English language, if report is not available in English, report in Swedish language is also suitable.
                - reply with only the link.
             
                If no link is found that matches the conditions, reply with 'No good candidate found'.
                """;
        StringBuilder message = new StringBuilder(m1).append(m2);

        for (Map<String, String> result : results) {
            message.append("\nTitle: ").append(result.get("title"))
                    .append("\nSnippet: ").append(result.get("snippet"))
                    .append("\nLink: ").append(result.get("link")).append("\n");
        }

        var userMessage = message.toString();
        //System.out.println("Test print: " + testPrint);
        System.out.println("Message: " + message);

        //var userMessage = new UserMessage(message.toString());
        //Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        var response = chatClient
                .prompt()
                .user(userMessage)
                .call()
                .content();

        log.info("LLM response: {}", response);
        return response;
    }

    // Alternative method to find the download links using ChatClient that uses GoogleSearchTool automatically
    public void findMeDownloadLink() {
        Pageable pageable = PageRequest.of(0, 10); // page 0, size 10
        List<Report> reports = reportRepository.findAll(pageable).getContent();

        for (Report report : reports) {
            if (report.getDownloadLink() != null) continue; // Skip already resolved ones
            String company = report.getCompanyName();

            String promptText = String.format(
                    "Your task is to find the direct download link for the official **2024 Annual Report** for the company **%s**. " +
                            "This report might also be called a **Year-End Report** or include **Q4 results**. " +
                            "The link MUST point directly to a **PDF file**. " +
                            "Please use the search tool, prioritizing the company's official 'Investor Relations' or 'Financial Reports' website sections. " +
                            "If you find the direct PDF link, return **only the URL** and nothing else. " +
                            "If you cannot find a direct PDF link after searching, respond with the exact text: **'Link not found'**.",
                    company // Pass the company name variable here
            );

            var response = chatClient
                    .prompt()
                    .user(promptText)
                    .tools(new GoogleSearchTool(searchService))
                    .call()
                    .content();
            String link = extractLinkFrom(response);
            log.info("Extracted link: {}", link);
        }
    }

    private String extractLinkFrom(String response) {
        return Arrays.stream(response.split("\\s+"))
                .filter(token -> token.startsWith("http"))
                .map(token -> token.replaceAll("[()<>,]", "")) // remove punctuation
                .findFirst()
                .orElse(null);
    }


}