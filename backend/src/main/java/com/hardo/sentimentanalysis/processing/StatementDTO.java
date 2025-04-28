package com.hardo.sentimentanalysis.processing;

public record StatementDTO(
        Statement.Type type,
        String companyName,
        String sector,
        String category,
        String content,
        String sentiment,
        String period

) {}