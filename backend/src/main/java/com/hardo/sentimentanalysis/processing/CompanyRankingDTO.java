package com.hardo.sentimentanalysis.processing;

public record CompanyRankingDTO(
        Long companyId,
        int rank,
        String companyName,
        String sector,
        String rationale
) {}
