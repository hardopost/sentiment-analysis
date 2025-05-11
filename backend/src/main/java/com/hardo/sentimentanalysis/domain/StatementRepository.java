package com.hardo.sentimentanalysis.domain;

import com.hardo.sentimentanalysis.analytics.dto.SentimentDataDTO;
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
    List<SentimentDataDTO> getSentimentsCounts(@Param("type") String type);

}
