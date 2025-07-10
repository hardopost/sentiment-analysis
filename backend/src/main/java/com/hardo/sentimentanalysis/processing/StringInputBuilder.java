package com.hardo.sentimentanalysis.processing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hardo.sentimentanalysis.domain.Report;
import com.hardo.sentimentanalysis.domain.Sector;
import com.hardo.sentimentanalysis.domain.Statement;


public class StringInputBuilder {

    public static String buildStringInputForStatementEmbedding(Statement statement, Report report) {
        return String.format("""
            Market: %s
            Company: %s
            Period: %s
            Type: %s
            Sector: %s
            Category: %s
            Sentiment: %s
            Content: %s
            Capitalization: %s
            """,
                report.getMarket(),
                report.getCompanyName(),
                report.getPeriod(),
                statement.getType(),
                statement.getSector(),
                statement.getCategory(),
                statement.getSentiment(),
                statement.getContent(),
                statement.getCapitalization()
        );
    }

    public static String buildStringInputForReportEmbedding(Report report, CompanySummaryDTO summary) {

        return String.format("""
        Market: %s
        Company: %s
        Period: %s
        Capitalization: %s
        Sector: %s
        Summary: %s
        Tone: %s
        Positive Drivers: %s
        Negative Drivers: %s
        """,
                report.getMarket(),
                report.getCompanyName(),
                report.getPeriod(),
                report.getCapitalization(),
                report.getSector(),
                summary.summary(),
                summary.tone(),
                String.join(", ", summary.positiveDrivers()),
                String.join(", ", summary.negativeDrivers()));
    }

    public static String buildStringInputForSectorStatement(Statement statement) {
        return String.format(
                "[Company: %s] [Period: %s] [Sector: %s] [Capitalization: %s] [Category: %s] [Sentiment: %s] %s",
                statement.getCompanyName(),
                statement.getPeriod(),
                statement.getSector(),
                statement.getCapitalization(),
                statement.getCategory(),
                statement.getSentiment(),
                statement.getContent()
        );
    }

    public static String buildStringInputForSectorsRanking(Sector sector) {
        return String.format("""
        Sector: %s
        Summary: %s
        Sentiment Justification: %s
        Sentiment: %s
        
        """,
                sector.getSectorName(),
                sector.getSummary(),
                sector.getSentimentJustification(),
                sector.getSentiment()
        );
    }

    public static String buildStringInputForCompanyRanking(Report report) {
        String companyId = report.getId().toString();
        String companyName = report.getCompanyName();
        String sectorName = report.getSector();
        String companyType = report.getCapitalization();
        String originalJson = report.getMetadata(); //JSONB string from database


        return String.format("""
        {       "companyId": "%s",
                "companyName": "%s",
                "sectorName": "%s",
                "companyType: "%s",
                           %s
        }
        """,companyId, companyName, sectorName, companyType, originalJson.substring(1)); // Removes opening '{' from original JSON
    }

    public static String buildStringInputValidationStatement(Statement statement) {
        return String.format(
                """
                   Company: %s,
                   Type: %s,
                   Sector: %s,
                   Category: %s,
                   Sentiment: %s,
                   Statement: %s
                """,
                statement.getCompanyName(),
                statement.getType(),
                statement.getSector(),
                statement.getCategory(),
                statement.getSentiment(),
                statement.getContent()
        );
    }

}
