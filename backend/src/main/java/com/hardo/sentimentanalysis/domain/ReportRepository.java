package com.hardo.sentimentanalysis.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("SELECT r FROM Report r WHERE r.sector = :sector AND r.period = :period")
    List<Report> findReportsBySectorAndPeriod(@Param("sector") String sector, @Param("period") String period);

}