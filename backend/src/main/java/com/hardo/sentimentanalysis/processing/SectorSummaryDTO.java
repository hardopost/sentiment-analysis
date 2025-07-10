package com.hardo.sentimentanalysis.processing;

public record SectorSummaryDTO(
        String summary,             // 300–400 word outlook
        String sentiment, // Expected: "very positive", positive", "neutral", "negative", very negative
        String sentimentJustification  // 150–200 word reasoning

)
{}

