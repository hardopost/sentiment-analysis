package com.hardo.sentimentanalysis.search;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class GoogleSearchTool {

    private final GoogleSearchService searchService;

    public GoogleSearchTool(GoogleSearchService googleSearchService) {
        this.searchService = googleSearchService;
    }

    @PostConstruct
    public void init() {
        System.out.println("✅ ReportSearchTool loaded as a Spring Tool bean!");
    }

    @Tool(description = "Search for company annual reports using Google. Returns a structured list of results.")
    public Map<String, Object> searchCompanyAnnualReports(String query) { // Return type is Map
        System.out.println("Tool: Searching for: " + query);
        String rawJsonResponse = searchService.fetchRawSearchResults(query);
        return parseResultsToMap(rawJsonResponse);
    }

    private Map<String, Object> parseResultsToMap(String rawJsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(rawJsonResponse);
            JsonNode itemsNode = root.path("items");

            if (itemsNode.isMissingNode() || !itemsNode.isArray() || itemsNode.isEmpty()) {
                // Return a map indicating no results
                return Map.of("searchResults", Collections.emptyList(), "message", "No relevant search results found.");
            }

            // Parse the items array into a List of Maps
            List<Map<String, Object>> itemsList = objectMapper.convertValue(itemsNode, new TypeReference<List<Map<String, Object>>>() {});

            // Return the list wrapped in a map
            return Map.of("searchResults", itemsList); // Key "searchResults" is arbitrary but descriptive

        } catch (Exception e) {
            System.err.println("❌ Failed to parse search results into map: " + e.getMessage());
            // Return a map indicating an error
            return Map.of("error", "Failed to process search results: " + e.getMessage());
        }
    }
}