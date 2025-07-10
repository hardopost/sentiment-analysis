package com.hardo.sentimentanalysis.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "statement_validation")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatementValidation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_id", nullable = false)
    private Long reportId;

    @Column(name = "given_statement", columnDefinition = "TEXT", nullable = false)
    private String givenStatement;

    @Column(name = "found_statement", columnDefinition = "TEXT")
    private String foundStatement;

    @Column(name = "match_found", nullable = false)
    private boolean matchFound;

    @Column(name = "is_type_valid", nullable = false)
    private boolean isTypeValid;

    @Column(name = "is_category_valid", nullable = false)
    private boolean isCategoryValid;

    @Column(name = "is_sentiment_valid", nullable = false)
    private boolean isSentimentValid;

    @Column(name = "is_sector_valid", nullable = false)
    private boolean isSectorValid;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "esg", nullable = false)
    private String esg; // "no", "yes-financial", or "yes-nonfinancial"


}