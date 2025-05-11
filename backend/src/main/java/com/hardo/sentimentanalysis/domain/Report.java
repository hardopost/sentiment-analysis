// Report.java
package com.hardo.sentimentanalysis.domain;

import com.hardo.sentimentanalysis.processing.OutlookSummaryDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "capitalization")
    private String capitalization; // Large, Mid, Small

    @Column(name = "sector")
    private String sector; // One-level sector from Nasdaq

    @Column(name = "period")
    private String period; // Example: "2024", "Q1-2025"

    @Column(name = "report_type")
    private String reportType; // Annual and Sustainability, or Annual only

    @Column(name = "download_link")
    private String downloadLink;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "processed")
    private boolean processed = false; // Flag to indicate if the report has been processed

    @Column(name = "downloaded")
    private boolean downloaded = false; // Flag to indicate if the report has been downloaded

    @Column(name = "statement_prompt_tokens")
    private Integer promptTokens;

    @Column(name = "statement_completion_tokens")
    private Integer completionTokens;

    @Column(name = "statement_total_tokens")
    private Integer totalTokens;

    @Column(name = "outlook_summary", columnDefinition = "jsonb")
    private String outlookSummary;

    @Column(name = "llm_time_ms")
    private Long llmTimeMs;

    @Column(name = "embedding_time_ms")
    private Long embeddingTimeMs;

    @Column(name = "embedding_total_tokens")
    private Integer embeddingTotalTokens;
}