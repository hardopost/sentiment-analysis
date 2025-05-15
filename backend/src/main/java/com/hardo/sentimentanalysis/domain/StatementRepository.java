package com.hardo.sentimentanalysis.domain;

import com.hardo.sentimentanalysis.analytics.dto.CategoryCountsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StatementRepository extends JpaRepository<Statement, UUID> {

    @Query(value = """
            SELECT
                s.category,
                SUM(CASE WHEN s.sentiment = 'negative' THEN 1 ELSE 0 END) AS negativeCount,
                SUM(CASE WHEN s.sentiment = 'neutral' THEN 1 ELSE 0 END) AS neutralCount,
                SUM(CASE WHEN s.sentiment = 'positive' THEN 1 ELSE 0 END) AS positiveCount
            FROM statement s
            WHERE s.type = :type
            GROUP BY s.category
            """, nativeQuery = true)
    List<CategoryCountsDTO> getSentimentsCounts(@Param("type") String type);

    @Query(value = """
            SELECT
                s.category,
                SUM(CASE WHEN s.sentiment = 'negative' THEN 1 ELSE 0 END) AS negativeCount,
                SUM(CASE WHEN s.sentiment = 'neutral' THEN 1 ELSE 0 END) AS neutralCount,
                SUM(CASE WHEN s.sentiment = 'positive' THEN 1 ELSE 0 END) AS positiveCount
            FROM statement s
            WHERE s.type = :type AND s.sector = :sector AND s.period = :period
            GROUP BY s.category
            """, nativeQuery = true)
    List<CategoryCountsDTO> getSectorSentimentsCounts(@Param("type") String type, @Param("sector") String sector, @Param("period") String period);

    @Query(value = """
            SELECT
                s.category,
                SUM(CASE WHEN s.sentiment = 'negative' THEN 1 ELSE 0 END) AS negativeCount,
                SUM(CASE WHEN s.sentiment = 'neutral' THEN 1 ELSE 0 END) AS neutralCount,
                SUM(CASE WHEN s.sentiment = 'positive' THEN 1 ELSE 0 END) AS positiveCount
            FROM statement s
            WHERE s.type = :type AND s.period = :period AND s.report_id = :reportId
            GROUP BY s.category
            """, nativeQuery = true)
    List<CategoryCountsDTO> getCompanySentimentsCounts(@Param("type") String type, @Param("reportId") Long reportId, @Param("period") String period);

    @Query("""
    SELECT s FROM Statement s
    WHERE s.type = :type AND s.sector = :sector AND s.period = :period
    ORDER BY s.category, s.sentiment
""")
    List<Statement> findByTypeAndSectorOrderByCategory(@Param("type") Statement.Type type, @Param("sector") String sector, @Param("period") String period);

    @Query("""
    SELECT s FROM Statement s
    WHERE s.reportId = :reportId
    ORDER BY s.category, s.sentiment
""")
    List<Statement> findByReportId(@Param("reportId") Long reportId);

}
