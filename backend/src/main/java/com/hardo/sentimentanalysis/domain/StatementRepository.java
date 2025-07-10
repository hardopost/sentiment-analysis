package com.hardo.sentimentanalysis.domain;

import com.hardo.sentimentanalysis.analytics.dto.CategoryCountsDTO;
import com.hardo.sentimentanalysis.analytics.dto.StatementDTO;
import com.hardo.sentimentanalysis.analytics.dto.StatementStatsDTO;
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
            WHERE s.type = :type AND s.market = :market
            GROUP BY s.category
            """, nativeQuery = true)
    List<CategoryCountsDTO> getSentimentsCounts(@Param("type") String type, @Param("market") String market);

    @Query(value = """
            SELECT
                s.category,
                SUM(CASE WHEN s.sentiment = 'negative' THEN 1 ELSE 0 END) AS negativeCount,
                SUM(CASE WHEN s.sentiment = 'neutral' THEN 1 ELSE 0 END) AS neutralCount,
                SUM(CASE WHEN s.sentiment = 'positive' THEN 1 ELSE 0 END) AS positiveCount
            FROM statement s
            WHERE s.type = :type AND s.sector = :sector AND s.period = :period AND s.market = :market
            GROUP BY s.category
            """, nativeQuery = true)
    List<CategoryCountsDTO> getSectorSentimentsCounts(@Param("type") String type, @Param("sector") String sector, @Param("period") String period, @Param("market") String market);

    @Query(value = """
            SELECT
                s.category,
                SUM(CASE WHEN s.sentiment = 'negative' THEN 1 ELSE 0 END) AS negativeCount,
                SUM(CASE WHEN s.sentiment = 'neutral' THEN 1 ELSE 0 END) AS neutralCount,
                SUM(CASE WHEN s.sentiment = 'positive' THEN 1 ELSE 0 END) AS positiveCount
            FROM statement s
            WHERE s.type = :type AND s.period = :period AND s.report_id = :reportId AND s.market = :market
            GROUP BY s.category
            """, nativeQuery = true)
    List<CategoryCountsDTO> getCompanySentimentsCounts(@Param("type") String type, @Param("reportId") Long reportId, @Param("period") String period, @Param("market") String market);

    @Query("""
    SELECT s FROM Statement s
    WHERE s.type = :type AND s.sector = :sector AND s.period = :period AND s.market = :market
    ORDER BY s.category, s.sentiment
""")
    List<Statement> findByTypeAndSectorOrderByCategory(@Param("type") Statement.Type type, @Param("sector") String sector, @Param("period") String period, @Param("market") String market);

    @Query("""
    SELECT s FROM Statement s
    WHERE s.reportId = :reportId
    ORDER BY s.category, s.sentiment
""")
    List<Statement> findByReportId(@Param("reportId") Long reportId);

    @Query(value = """
    SELECT s.company_name, s.content FROM Statement s
    WHERE s.type = :type AND s.sector = :sector AND s.category = :category AND s.sentiment = :sentiment AND s.period = :period AND s.market = :market
    GROUP BY s.company_name, s.content
    ORDER BY s.company_name
    """, nativeQuery = true)
    List<StatementDTO> findByTypeAndSectorAndCategoryAndSentiment(
            @Param("type") String type,
            @Param("sector") String sector,
            @Param("category") String category,
            @Param("sentiment") String sentiment,
            @Param("period") String period,
            @Param("market") String market);

    @Query(value = """
    SELECT s.company_name, s.content FROM Statement s
    WHERE s.type = :type AND s.company_name = :companyName AND s.category = :category AND s.sentiment = :sentiment AND s.period = :period
    GROUP BY s.company_name, s.content
    ORDER BY s.company_name
    """, nativeQuery = true)
    List<StatementDTO> findByTypeAndCompanyAndCategoryAndSentiment(
            @Param("type") String type,
            @Param("companyName") String companyName,
            @Param("category") String category,
            @Param("sentiment") String sentiment,
            @Param("period") String period);

    @Query(value = """
    SELECT 
        (SELECT COUNT(*) FROM statement) AS totalStatements,
        (SELECT COUNT(*) FROM statement WHERE type = 'company') AS companyStatements,
        (SELECT COUNT(*) FROM statement WHERE type = 'sector') AS sectorStatements,
        (SELECT COUNT(*) FROM statement WHERE type = 'company' AND market = 'stockholm') AS companyStatementsStockholm,
        (SELECT COUNT(*) FROM statement WHERE type = 'company' AND market = 'baltic') AS companyStatementsBaltic,
        (SELECT COUNT(*) FROM statement WHERE type = 'sector' AND market = 'stockholm') AS sectorStatementsStockholm,
        (SELECT COUNT(*) FROM statement WHERE type = 'sector' AND market = 'baltic') AS sectorStatementsBaltic
    """, nativeQuery = true)
    StatementStatsDTO getStatementStats();


}
