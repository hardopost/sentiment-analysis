package com.hardo.sentimentanalysis.processing;

public record SectorSummaryDTO(
        String summary,             // 300–400 word outlook
        String sentimentJustification,  // 150–200 word reasoning
        String tone // Expected: "very positive", positive", "neutral", "negative", very negative
)
{}

