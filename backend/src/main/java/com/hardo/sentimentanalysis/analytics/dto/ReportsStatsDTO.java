package com.hardo.sentimentanalysis.analytics.dto;

import lombok.*;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ReportsStatsDTO {
    private long totalReports;
    private Map<String, Long> marketCounts;
    private Map<String, Long> reportTypeCounts;
    private Map<String, Long> fiscalYearCounts;
}
