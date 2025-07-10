package com.hardo.sentimentanalysis.analytics;

import com.hardo.sentimentanalysis.analytics.dto.*;
import org.springframework.web.bind.annotation.*;

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
    public List<ChartsAndSectorSummariesDTO> getSectorsData(@RequestParam String market) {
        return analyticsService.getChartsAndSectorSummaries("2024", market);
    }

    @PostMapping("/sector-statements")
    public List<StatementDTO> getSectorStatements(@RequestBody SectorStatementsRequestDTO request) {
        return analyticsService.getSectorStatements(
                request.getType(),
                request.getSector(),
                request.getCategory(),
                request.getSentiment(),
                request.getMarket()
        );
    }

    @GetMapping("/company-names")
    public List<String> getCompanyNames() {
        return analyticsService.getCompanyNames();
    }

    @GetMapping("/company-summary")
    public ChartsAndTopCompaniesSummariesDTO getCompany(@RequestParam String companyName) {
        return analyticsService.getChartAndCompanySummary("2024", companyName);
    }

    @GetMapping("/company-summaries")
    public List<ChartsAndTopCompaniesSummariesDTO> getCompaniesData(@RequestParam String market) {
        return analyticsService.getChartsAndTopCompaniesSummaries("2024", market);
    }


    @PostMapping("/company-statements")
    public List<StatementDTO> getCompanyStatements(@RequestBody CompanyStatementsRequestDTO request) {
        return analyticsService.getCompanyStatements(
                request.getType(),
                request.getCompanyName(),
                request.getCategory(),
                request.getSentiment()
        );
    }

    @GetMapping("/info")
    public InfoPageStatsDTO getInfoPageStats() {
        return analyticsService.getInfoPageStats();
    }

}
