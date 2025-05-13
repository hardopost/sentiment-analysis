package com.hardo.sentimentanalysis.googlesearch;

import com.hardo.sentimentanalysis.config.GoogleSearchProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GoogleSearchService {

    private final WebClient webClient = WebClient.create();
    private final GoogleSearchProperties props;
    private final Logger log = LoggerFactory.getLogger(GoogleSearchService.class);

    public GoogleSearchService(GoogleSearchProperties props) {
        this.props = props;
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