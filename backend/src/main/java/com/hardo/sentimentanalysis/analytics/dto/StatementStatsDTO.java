package com.hardo.sentimentanalysis.analytics.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class StatementStatsDTO {
    private long totalStatements;
    private long companyStatements;
    private long sectorStatements;
    private long companyStatementsStockholm;
    private long companyStatementsBaltic;
    private long sectorStatementsStockholm;
    private long sectorStatementsBaltic;
}
