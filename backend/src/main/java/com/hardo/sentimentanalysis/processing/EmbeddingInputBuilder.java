package com.hardo.sentimentanalysis.processing;

public class EmbeddingInputBuilder {

    public static String buildEmbeddingInput(Statement statement) {
        return String.format("""
            Company: %s
            Period: %s
            Type: %s
            Industry: %s
            Supersector: %s
            Sector: %s
            Category: %s
            Sentiment: %s
            Statement: %s
            """,
                statement.getCompanyName(),
                statement.getPeriod(),
                statement.getType(),
                statement.getIndustry(),
                statement.getSupersector(),
                statement.getSector(),
                statement.getCategory(),
                statement.getSentiment(),
                statement.getContent()
        );
    }
}
