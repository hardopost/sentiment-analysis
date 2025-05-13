package com.hardo.sentimentanalysis.processing;

public record SectorRankingDTO(
        int rank,
        String sector,
        String rationale
) {}
