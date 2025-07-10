package com.hardo.sentimentanalysis.domain;

import com.hardo.sentimentanalysis.analytics.dto.ValidationStatsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StatementValidationRepository extends JpaRepository<StatementValidation, Long> {

    @Query(value = """
    SELECT
      SUM(CASE WHEN match_found = true THEN 1 ELSE 0 END) AS matchFoundTrue,
      SUM(CASE WHEN match_found = false THEN 1 ELSE 0 END) AS matchFoundFalse,
      SUM(CASE WHEN is_type_valid = true THEN 1 ELSE 0 END) AS typeValidTrue,
      SUM(CASE WHEN is_type_valid = false THEN 1 ELSE 0 END) AS typeValidFalse,
      SUM(CASE WHEN is_category_valid = true THEN 1 ELSE 0 END) AS categoryValidTrue,
      SUM(CASE WHEN is_category_valid = false THEN 1 ELSE 0 END) AS categoryValidFalse,
      SUM(CASE WHEN is_sentiment_valid = true THEN 1 ELSE 0 END) AS sentimentValidTrue,
      SUM(CASE WHEN is_sentiment_valid = false THEN 1 ELSE 0 END) AS sentimentValidFalse,
      SUM(CASE WHEN is_sector_valid = true THEN 1 ELSE 0 END) AS sectorValidTrue,
      SUM(CASE WHEN is_sector_valid = false THEN 1 ELSE 0 END) AS sectorValidFalse,
      SUM(CASE WHEN esg = 'yes-financial' THEN 1 ELSE 0 END) AS esgYesFinancial,
      SUM(CASE WHEN esg = 'yes-nonfinancial' THEN 1 ELSE 0 END) AS esgYesNonfinancial,
      SUM(CASE WHEN esg = 'no' THEN 1 ELSE 0 END) AS esgNo
    FROM statement_validation
""", nativeQuery = true)
    ValidationStatsDTO getValidationStats();


}
