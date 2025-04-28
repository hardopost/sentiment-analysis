package com.hardo.sentimentanalysis.processing;

public class EmbeddingInputBuilder {

    public static String buildEmbeddingInput(Statement statement) {
        return String.format("""
            Company: %s
            Period: %s
            Type: %s
            Sector: %s
            Category: %s
            Sentiment: %s
            Content: %s
            Capitalization: %s
            """,
                statement.getCompanyName(),
                statement.getPeriod(),
                statement.getType(),
                statement.getSector(),
                statement.getCategory(),
                statement.getSentiment(),
                statement.getContent(),
                statement.getCapitalization()
        );
    }
}
