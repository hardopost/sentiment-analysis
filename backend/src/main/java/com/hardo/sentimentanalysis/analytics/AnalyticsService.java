package com.hardo.sentimentanalysis.analytics;

import com.hardo.sentimentanalysis.analytics.dto.*;
import com.hardo.sentimentanalysis.domain.ReportRepository;
import com.hardo.sentimentanalysis.domain.SectorRepository;
import com.hardo.sentimentanalysis.domain.Statement;
import com.hardo.sentimentanalysis.domain.StatementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyticsService {

    private final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);
    private final StatementRepository statementRepository;
    private final SectorRepository sectorRepository;
    private final ReportRepository reportRepository;

    public AnalyticsService(StatementRepository statementRepository, SectorRepository sectorRepository, ReportRepository reportRepository) {
        this.statementRepository = statementRepository;
        this.sectorRepository = sectorRepository;
        this.reportRepository = reportRepository;
    }

    public CompanySectorAggregateCountsDTO getSentimentsData() {
        CompanySectorAggregateCountsDTO companySectorAggregateCountsDTO = new CompanySectorAggregateCountsDTO();
        companySectorAggregateCountsDTO.setCompanies(statementRepository.getSentimentsCounts("company"));
        companySectorAggregateCountsDTO.setSectors(statementRepository.getSentimentsCounts("sector"));
        return companySectorAggregateCountsDTO;
    }

    public List<ChartsAndSectorSummariesDTO> getChartsAndSectorSummaries(String period) {
        List<SectorSummariesDTO> rawSectors = sectorRepository.getSectorSummaries();

        List<ChartsAndSectorSummariesDTO> enriched = new ArrayList<>();

        for (SectorSummariesDTO baseDto : rawSectors) {
            ChartsAndSectorSummariesDTO wrapper = new ChartsAndSectorSummariesDTO(baseDto);
            List<CategoryCountsDTO> counts = statementRepository.getSectorSentimentsCounts("sector", baseDto.getSector(), period);
            wrapper.setCategoryCounts(counts);
            enriched.add(wrapper);
        }

        return enriched;
    }

    public List<ChartsAndTopCompaniesSummariesDTO> getChartsAndTopCompaniesSummaries(String period) {

        List<CompanySummariesDTO> rawCompanies = reportRepository.getCompanySummaries(period); // all companies that have a rank
        logger.info("Founds {} companies with rankings: ", rawCompanies.size());

        List<ChartsAndTopCompaniesSummariesDTO> enriched = new ArrayList<>();

        for (CompanySummariesDTO baseDto : rawCompanies) {
            ChartsAndTopCompaniesSummariesDTO wrapper = new ChartsAndTopCompaniesSummariesDTO(baseDto);
            List<CategoryCountsDTO> counts = statementRepository.getCompanySentimentsCounts("company", baseDto.getReportId(), period);
            wrapper.setCategoryCounts(counts);
            enriched.add(wrapper);
        }

        return enriched;
    }



}
