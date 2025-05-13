package com.hardo.sentimentanalysis.domain;

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
}
