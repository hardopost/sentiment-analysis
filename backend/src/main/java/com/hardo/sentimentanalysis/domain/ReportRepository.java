package com.hardo.sentimentanalysis.domain;

import com.hardo.sentimentanalysis.analytics.dto.CompanySummariesDTO;
import com.hardo.sentimentanalysis.analytics.dto.SectorSummariesDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("SELECT r FROM Report r WHERE r.sector = :sector AND r.period = :period")
    List<Report> findReportsBySectorAndPeriod(@Param("sector") String sector, @Param("period") String period);

    @Query(value = """
            SELECT
                s.id AS reportId,
                s.company_name AS companyName,
                s.sector AS sector,
                s.content AS summary,
                s.capitalization AS capitalization,
                s.rank AS sectorRank,
                s.rank_rationale AS rankRationale,
                s.period AS period
            FROM report s
            WHERE s.period = :period AND s.rank is NOT NULL
            GROUP BY s.id, s.company_name, s.sector, s.content, s.capitalization, s.rank, s.rank_rationale, s.period
            """, nativeQuery = true)
    List<CompanySummariesDTO> getCompanySummaries(@Param("period") String period);

}