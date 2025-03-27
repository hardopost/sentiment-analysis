package com.hardo.sentimentanalysis.chat;

import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.stream.Collectors;

public class StatementContextAdvisor implements CallAroundAdvisor {

    private final VectorStore vectorStore;

    public StatementContextAdvisor(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest request, CallAroundAdvisorChain chain) {
        String userPrompt = request.userText();


        List<Document> retrieved = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(userPrompt)
                        .topK(5)
                        .build()
        );

        String context = retrieved.stream()
                .map(doc -> {
                    String metadata = doc.getMetadata().entrySet().stream()
                            .map(e -> e.getKey() + ": " + e.getValue())
                            .collect(Collectors.joining("\n"));
                    return metadata + "\n" + doc.getText();
                })
                .collect(Collectors.joining("\n---\n"));

        String finalPrompt = "Context:\n" + context + "\n\nUser question:\n" + userPrompt;

        List<Message> messages = List.of(new UserMessage(finalPrompt));

        AdvisedRequest enrichedRequest = AdvisedRequest.from(request)
                .messages(messages)
                .build();

        return chain.nextAroundCall(enrichedRequest);
    }

    @Override
    public String getName() {
        return "StatementContextAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}