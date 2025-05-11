package com.hardo.sentimentanalysis.chat;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final VectorStore vectorStore;

    public RagService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * Retrieves relevant documents from the VectorStore based on the user query.
     */
    public List<Document> retrieveDocuments(String userQuery, int topK) {
        System.out.println("Retrieving documents from VectorStore for query: " + userQuery);
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(userQuery)
                        .topK(topK) // Number of top K documents to retrieve
                        .build()
                // You can add similarity thresholds or other filters here if needed
                // .withSimilarityThreshold(0.75)
        );
        System.out.println("Retrieved " + documents.size() + " documents.");
        return documents;
    }

    /**
     * Formats the retrieved documents into a string suitable for inclusion in the prompt context.
     */
    public String formatDocuments(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return "No relevant forward-looking statements found in the database for this query.";
        }

        return documents.stream()
                .map(doc -> {
                    // Extract metadata - Use consistent keys matching your data load
                    String company = doc.getMetadata().getOrDefault("companyName", "N/A").toString();
                    String type = doc.getMetadata().getOrDefault("type", "N/A").toString();
                    String category = doc.getMetadata().getOrDefault("category", "N/A").toString();
                    String sector = doc.getMetadata().getOrDefault("sector", "N/A").toString();
                    String period = doc.getMetadata().getOrDefault("period", "N/A").toString();
                    String sentiment = doc.getMetadata().getOrDefault("sentiment", "N/A").toString();
                    String content = doc.getMetadata().getOrDefault("content", "N/A").toString();
                    String capitalization = doc.getMetadata().getOrDefault("capitalization", "N/A").toString();

                    String output = String.format(
                            "----\nSource Document:\nCompany: %s\nStatement about: %s\nCategory: %s\nSector: %s\nPeriod: %s\nSentiment: %s\nStatement: %s\nCapitalization: %s\n----",
                            company, type, category, sector, period, sentiment, content, capitalization // Use getContent()
                    );

                    System.out.println("Formatted document:\n" + output); // Debugging output

                    // Format for clarity in the prompt
                    return output;
                })
                .collect(Collectors.joining("\n\n")); // Separate documents clearly
    }
}
