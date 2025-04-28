package com.hardo.sentimentanalysis.search;

import com.hardo.sentimentanalysis.config.GoogleSearchProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoogleSearchService {

    private final WebClient webClient = WebClient.create();
    private final GoogleSearchProperties props;
    private final Logger log = LoggerFactory.getLogger(GoogleSearchService.class);

    public GoogleSearchService(GoogleSearchProperties props) {
        this.props = props;
    }

    /**
     * Performs a Google search for the given query string and extracts detailed search results.
     * @param query the query string to search
     * @return a list of maps containing "title", "snippet", and "link" for each result
     */
    public List<Map<String, String>> search(String query) {
        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("www.googleapis.com")
                        .path("/customsearch/v1")
                        .queryParam("key", props.getApiKey())
                        .queryParam("cx", props.getEngineId())
                        .queryParam("q", query)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("✅ Google Search performed with query: {}", query);
        return extractSearchItems(response);
    }

    /**
     * Extracts a list of search result maps containing "title", "snippet", and "link" from a raw JSON response.
     * @param json the raw JSON string from Google Search API
     * @return a list of result maps
     */
    private List<Map<String, String>> extractSearchItems(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode items = root.path("items");

            List<Map<String, String>> results = new ArrayList<>();
            for (JsonNode item : items) {
                Map<String, String> result = new HashMap<>();
                result.put("title", item.path("title").asText());
                result.put("snippet", item.path("snippet").asText());
                result.put("link", item.path("link").asText());
                results.add(result);
            }
            return results;
        } catch (Exception e) {
            System.err.println("❌ Failed to parse search JSON: " + e.getMessage());
            return List.of(Map.of("error", "Failed to parse search results"));
        }
    }

    public String fetchRawSearchResults(String query) { // Renamed method
        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("www.googleapis.com")
                        .path("/customsearch/v1")
                        .queryParam("key", props.getApiKey())
                        .queryParam("cx", props.getEngineId())
                        .queryParam("q", query)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                // Add error handling for the webClient call itself
                .doOnError(e -> log.error("❌ Google Search API call failed for query [{}]: {}", query, e.getMessage()))
                .block(); // Consider using reactive flow if possible instead of block()

        if (response != null) {
            log.info("✅ Google Search performed with query: {}", query);
        } else {
            log.warn("⚠️ Google Search returned null response for query: {}", query);
            return "{}"; // Return empty JSON object string if API fails
        }
        return response; // Return the full JSON response string
    }


}