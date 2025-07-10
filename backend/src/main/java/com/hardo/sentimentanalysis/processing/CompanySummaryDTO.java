package com.hardo.sentimentanalysis.processing;

import java.util.List;

public record CompanySummaryDTO(
        String summary,
        String tone,           // "positive", "neutral", or "negative"
        List<String> positiveDrivers,   // ["innovation", "clean tech"]
        List<String> negativeDrivers,  // ["supply chain disruptions", "inflationary pressure"]
        String reportType, // Annual Report, Annual and Sustainability Report etc.
        String reportTitleFull, //2024 Annual and Sustainability Report, 2023/2024 Annual Report, etc.
        String reportFiscalYear // 2024, 2023/2024, etc.
) {}
