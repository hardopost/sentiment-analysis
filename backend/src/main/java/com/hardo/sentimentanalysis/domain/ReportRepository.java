package com.hardo.sentimentanalysis.domain;

import com.hardo.sentimentanalysis.analytics.dto.CompanySummaryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("SELECT r FROM Report r WHERE r.id = :id")
    Report findReportById(@Param("id") Long id);

    @Query("SELECT r FROM Report r WHERE r.sector = :sector AND r.period = :period AND r.market = :market")
    List<Report> findReportsBySectorAndPeriod(@Param("sector") String sector,
                                              @Param("period") String period,
                                              @Param("market") String market);

    @Query("SELECT DISTINCT r.companyName FROM Report r")
    List<String> findAllCompanyNames();

    @Query(value = """
            SELECT
                s.id AS reportId,
                s.company_name AS companyName,
                s.sector AS sector,
                s.metadata ->> 'summary' AS summary,
                s.metadata ->> 'positiveDrivers' AS positiveDrivers,
                s.metadata ->> 'negativeDrivers' AS negativeDrivers,
                s.capitalization AS capitalization,
                s.rank AS sectorRank,
                s.rank_rationale AS rankRationale,
                s.period AS period,
                s.market AS market
            FROM report s
            WHERE s.period = :period AND s.company_name = :companyName
            GROUP BY s.id, s.company_name, s.sector, s.metadata, s.capitalization, s.rank, s.rank_rationale, s.period, s.market
            """, nativeQuery = true)
    CompanySummaryDTO getCompanySummary(@Param("period") String period, @Param("companyName") String companyName);

    @Query(value = """
            SELECT
                s.id AS reportId,
                s.company_name AS companyName,
                s.sector AS sector,
                s.metadata ->> 'summary' AS summary,
                s.metadata ->> 'positiveDrivers' AS positiveDrivers,
                s.metadata ->> 'negativeDrivers' AS negativeDrivers,
                s.capitalization AS capitalization,
                s.rank AS sectorRank,
                s.rank_rationale AS rankRationale,
                s.period AS period,
                s.market AS market
            FROM report s
            WHERE s.period = :period AND s.rank is NOT NULL AND s.market = :market
            GROUP BY s.id, s.company_name, s.sector, s.metadata, s.capitalization, s.rank, s.rank_rationale, s.period, s.market
            """, nativeQuery = true)
    List<CompanySummaryDTO> getCompanySummaries(@Param("period") String period, @Param("market") String market);

    @Query(value = "SELECT report_type, COUNT(*) FROM report GROUP BY report_type", nativeQuery = true)
    List<Object[]> countByReportType();

    @Query(value = "SELECT report_fiscal_year, COUNT(*) FROM report GROUP BY report_fiscal_year", nativeQuery = true)
    List<Object[]> countByFiscalYear();

    @Query(value = "SELECT COUNT(*) FROM report", nativeQuery = true)
    long countTotalReports();

    @Query(value = "SELECT market, COUNT(*) FROM report GROUP BY market", nativeQuery = true)
    List<Object[]> countByMarket();




}