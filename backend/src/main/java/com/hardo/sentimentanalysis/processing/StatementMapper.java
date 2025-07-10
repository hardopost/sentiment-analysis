package com.hardo.sentimentanalysis.processing;

import com.hardo.sentimentanalysis.domain.Report;
import com.hardo.sentimentanalysis.domain.Statement;

import java.util.UUID;

public class StatementMapper {

    public static Statement fromReportAndStatementDTO(StatementDTO dto, Report report) {
        return new Statement(
                UUID.randomUUID(),
                report.getMarket(),
                dto.type(),
                report.getCompanyName(),
                report.getSector(),
                dto.category(),
                dto.content(),
                dto.sentiment().toLowerCase(),
                report.getPeriod(),
                report.getCapitalization(),
                report.getId()
        );
    }
}