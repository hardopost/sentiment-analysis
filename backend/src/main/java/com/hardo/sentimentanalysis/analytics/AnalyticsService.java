package com.hardo.sentimentanalysis.analytics;

import com.hardo.sentimentanalysis.analytics.dto.*;
import com.hardo.sentimentanalysis.domain.ReportRepository;
import com.hardo.sentimentanalysis.domain.SectorRepository;
import com.hardo.sentimentanalysis.domain.StatementRepository;
import com.hardo.sentimentanalysis.domain.StatementValidationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);
    private final StatementRepository statementRepository;
    private final SectorRepository sectorRepository;
    private final ReportRepository reportRepository;
    private final StatementValidationRepository statementValidationRepository;

    public AnalyticsService(StatementRepository statementRepository, SectorRepository sectorRepository, ReportRepository reportRepository, StatementValidationRepository statementValidationRepository) {
        this.statementRepository = statementRepository;
        this.sectorRepository = sectorRepository;
        this.reportRepository = reportRepository;
        this.statementValidationRepository = statementValidationRepository;
    }

    public CompanySectorAggregateCountsDTO getSentimentsData() {
        CompanySectorAggregateCountsDTO dto = new CompanySectorAggregateCountsDTO();

        // Create and populate the company map
        Map<String, List<CategoryCountsDTO>> companyMap = new HashMap<>();
        companyMap.put("stockholm", statementRepository.getSentimentsCounts("company", "stockholm"));
        companyMap.put("baltic", statementRepository.getSentimentsCounts("company", "baltic"));

        // Create and populate the sector map
        Map<String, List<CategoryCountsDTO>> sectorMap = new HashMap<>();
        sectorMap.put("stockholm", statementRepository.getSentimentsCounts("sector", "stockholm"));
        sectorMap.put("baltic", statementRepository.getSentimentsCounts("sector", "baltic"));

        // Set both maps in the DTO
        dto.setCompanies(companyMap);
        dto.setSectors(sectorMap);

        return dto;
    }

    public List<ChartsAndSectorSummariesDTO> getChartsAndSectorSummaries(String period, String market) {
        // Normalize and validate market
        String normalizedMarket = market.toLowerCase();
        if (!normalizedMarket.equals("stockholm") && !normalizedMarket.equals("baltic")) {
            throw new IllegalArgumentException("Invalid market: " + market + ". Expected 'stockholm' or 'baltic'.");
        }

        // Fetch sector summaries
        List<SectorSummariesDTO> rawSectors = sectorRepository.getSectorSummaries(normalizedMarket);
        List<ChartsAndSectorSummariesDTO> enriched = new ArrayList<>();

        for (SectorSummariesDTO baseDto : rawSectors) {
            ChartsAndSectorSummariesDTO wrapper = new ChartsAndSectorSummariesDTO(baseDto);
            List<CategoryCountsDTO> counts = statementRepository.getSectorSentimentsCounts(
                    "sector",
                    baseDto.getSector(),
                    period,
                    normalizedMarket
            );
            wrapper.setCategoryCounts(counts);
            enriched.add(wrapper);
        }

        return enriched;
    }

    public List<String> getCompanyNames() {
        return reportRepository.findAllCompanyNames();
    }

    public ChartsAndTopCompaniesSummariesDTO getChartAndCompanySummary(String period, String companyName) {
        logger.info("Fetching summary for company: {}", companyName);

        // Fetch company summaries
        CompanySummaryDTO rawCompany = reportRepository.getCompanySummary(period, companyName);
        logger.info("Found company with name {}: ", rawCompany.getCompanyName());

        ChartsAndTopCompaniesSummariesDTO wrapper = new ChartsAndTopCompaniesSummariesDTO(rawCompany);

        if (rawCompany.getCompanyName().equalsIgnoreCase(companyName)) {
                List<CategoryCountsDTO> counts = statementRepository.getCompanySentimentsCounts(
                        "company", rawCompany.getReportId(), period, rawCompany.getMarket()
                );
                wrapper.setCategoryCounts(counts);
        } else {
            logger.warn("Company name mismatch: expected {}, found {}", companyName, rawCompany.getCompanyName());
            throw new IllegalArgumentException("Company not found: " + companyName);
        }

        return wrapper;
    }

    public List<ChartsAndTopCompaniesSummariesDTO> getChartsAndTopCompaniesSummaries(String period, String market) {

        List<CompanySummaryDTO> rawCompanies = reportRepository.getCompanySummaries(period, market); // all companies that have a rank
        logger.info("Founds {} companies with rankings: ", rawCompanies.size());

        List<ChartsAndTopCompaniesSummariesDTO> enriched = new ArrayList<>();

        for (CompanySummaryDTO baseDto : rawCompanies) {
            ChartsAndTopCompaniesSummariesDTO wrapper = new ChartsAndTopCompaniesSummariesDTO(baseDto);
            List<CategoryCountsDTO> counts = statementRepository.getCompanySentimentsCounts("company", baseDto.getReportId(), period, market);
            wrapper.setCategoryCounts(counts);
            enriched.add(wrapper);
        }

        return enriched;
    }

    public List<StatementDTO> getSectorStatements(String type, String sector, String category, String sentiment, String market) {
        return statementRepository.findByTypeAndSectorAndCategoryAndSentiment(type, sector, category, sentiment, "2024", market);
    }

    public List<StatementDTO> getCompanyStatements(String type, String companyName, String category, String sentiment) {
        return statementRepository.findByTypeAndCompanyAndCategoryAndSentiment(type, companyName, category, sentiment, "2024");
    }

    public InfoPageStatsDTO getInfoPageStats() {
        ValidationStatsDTO validationStats = statementValidationRepository.getValidationStats(); // existing logic

        ReportsStatsDTO reportsStats = new ReportsStatsDTO();
        Map<String, Long> typeCounts = reportRepository.countByReportType().stream()
                .collect(Collectors.toMap(
                        row -> String.valueOf(row[0]),
                        row -> ((Number) row[1]).longValue()
                ));

        Map<String, Long> fiscalCounts = reportRepository.countByFiscalYear().stream()
                .collect(Collectors.toMap(
                        row -> String.valueOf(row[0]),
                        row -> ((Number) row[1]).longValue()
                ));

        Map<String, Long> marketCounts = reportRepository.countByMarket().stream()
                .collect(Collectors.toMap(
                        row -> String.valueOf(row[0]),
                        row -> ((Number) row[1]).longValue()
                ));
        long total = reportRepository.countTotalReports();

        reportsStats.setReportTypeCounts(typeCounts);
        reportsStats.setFiscalYearCounts(fiscalCounts);
        reportsStats.setMarketCounts(marketCounts);
        reportsStats.setTotalReports(total);

        StatementStatsDTO statementStats = statementRepository.getStatementStats();





        return new InfoPageStatsDTO(validationStats, reportsStats, statementStats);
    }

}
