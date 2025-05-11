package com.hardo.sentimentanalysis.processing;

import com.hardo.sentimentanalysis.domain.Report;
import com.hardo.sentimentanalysis.domain.Statement;


public class EmbeddingInputBuilder {

    public static String buildEmbeddingInputForStatement(Statement statement) {
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

    public static String buildEmbeddingInputForReport(Report report, OutlookSummaryDTO summary) {

        return String.format("""
        Company: %s
        Period: %s
        Capitalization: %s
        Report Type: %s
        Summary: %s
        Tone: %s
        Positive Drivers: %s
        Negative Drivers: %s
        """,
                report.getCompanyName(),
                report.getPeriod(),
                report.getCapitalization(),
                summary.reportType(),
                summary.summary(),
                summary.tone(),
                String.join(", ", summary.positiveDrivers()),
                String.join(", ", summary.negativeDrivers()));
    }
}
