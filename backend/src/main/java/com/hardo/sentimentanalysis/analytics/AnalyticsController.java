package com.hardo.sentimentanalysis.analytics;

import com.hardo.sentimentanalysis.analytics.dto.AllSectorSummariesAndChartsDTO;
import com.hardo.sentimentanalysis.analytics.dto.ChartsAndSectorSummariesDTO;
import com.hardo.sentimentanalysis.analytics.dto.ChartsAndTopCompaniesSummariesDTO;
import com.hardo.sentimentanalysis.analytics.dto.CompanySectorAggregateCountsDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/sentiments")
    public CompanySectorAggregateCountsDTO getSentimentsData() {
        return analyticsService.getSentimentsData();
    }

    @GetMapping("/sector-summaries")
    public List<ChartsAndSectorSummariesDTO> getSectorsData() {
        return analyticsService.getChartsAndSectorSummaries("2024");
    }

    @GetMapping("/company-summaries")
    public List<ChartsAndTopCompaniesSummariesDTO> getCompaniesData() {
        return analyticsService.getChartsAndTopCompaniesSummaries("2024");
    }

}
