package com.hardo.sentimentanalysis.processing;

import com.hardo.sentimentanalysis.domain.Statement;

public record StatementDTO(
        Statement.Type type,
        String category,
        String content,
        String sentiment
) {}