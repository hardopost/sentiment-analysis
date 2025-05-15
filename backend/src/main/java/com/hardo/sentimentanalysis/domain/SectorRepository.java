package com.hardo.sentimentanalysis.domain;

import com.hardo.sentimentanalysis.analytics.dto.SectorSummariesDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SectorRepository extends JpaRepository<Sector, Long> {

    @Query("""
    SELECT s FROM Sector s
    WHERE s.period = :period
    """)
    List<Sector> findSummariesByPeriod(@Param("period") String period);

    @Query("""
    SELECT s FROM Sector s
    WHERE s.period = :period AND s.sectorName = :sectorName
    """)
    Sector findSummaryBySector(@Param("sectorName") String name, @Param("period") String period);

    @Query(value = """
            SELECT
                s.sector_name AS sector,
                s.summary AS summary,
                s.tone AS tone,
                s.sentiment_justification AS toneRationale,
                s.ranking AS ranking,
                s.rationale AS rankingRationale
            FROM sector s
            GROUP BY s.sector_name, s.summary, s.tone, s.sentiment_justification, s.ranking, s.rationale
            """, nativeQuery = true)
    List<SectorSummariesDTO> getSectorSummaries();

}
