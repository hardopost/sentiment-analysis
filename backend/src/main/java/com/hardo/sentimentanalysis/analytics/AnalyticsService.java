package com.hardo.sentimentanalysis.analytics;

import com.hardo.sentimentanalysis.analytics.dto.SentimentDataSummaryDTO;
import com.hardo.sentimentanalysis.domain.StatementRepository;
import org.springframework.stereotype.Service;
import com.hardo.sentimentanalysis.analytics.dto.SentimentDataDTO;

import java.util.List;

@Service
public class AnalyticsService {

    private final StatementRepository statementRepository;

    public AnalyticsService(StatementRepository statementRepository) {
        this.statementRepository = statementRepository;

    }

    public SentimentDataSummaryDTO getSentimentsData() {
        SentimentDataSummaryDTO sentimentDataSummaryDTO = new SentimentDataSummaryDTO();
        sentimentDataSummaryDTO.setCompanies(statementRepository.getSentimentsCounts("company"));
        sentimentDataSummaryDTO.setSectors(statementRepository.getSentimentsCounts("sector"));
        return sentimentDataSummaryDTO;
    }

}
