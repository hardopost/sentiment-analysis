package com.hardo.sentimentanalysis.processing;

public record StatementValidationDTO(
        String givenStatement,
        String foundStatement,
        boolean matchFound,
        boolean isTypeValid,
        boolean isCategoryValid,
        boolean isSentimentValid,
        boolean isSectorValid,
        String comment
) {}
