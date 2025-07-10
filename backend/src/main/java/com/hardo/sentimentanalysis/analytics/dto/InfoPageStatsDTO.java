package com.hardo.sentimentanalysis.analytics.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class InfoPageStatsDTO {
    private ValidationStatsDTO validationStats;
    private ReportsStatsDTO reportsStats;
    private StatementStatsDTO statementStats;

}
