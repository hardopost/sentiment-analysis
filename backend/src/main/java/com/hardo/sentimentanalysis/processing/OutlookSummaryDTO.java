package com.hardo.sentimentanalysis.processing;

import java.util.List;

public record OutlookSummaryDTO(
        String reportType, // Annual, Year-end and Q4, Annual and Sustainability, etc.
        String summary,
        String tone,           // "positive", "neutral", or "negative"
        List<String> positiveDrivers,   // ["innovation", "clean tech"]
        List<String> negativeDrivers  // ["supply chain disruptions", "inflationary pressure"]
) {}
