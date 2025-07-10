package com.hardo.sentimentanalysis.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sector")
public class Sector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sector_name", nullable = false, unique = true)
    private String sectorName;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "sentiment_justification", columnDefinition = "TEXT")
    private String sentimentJustification;

    @Column(name = "sentiment")
    private String sentiment; // Expected: "positive", "neutral", or "negative"

    @Column(name = "period")
    private String period; // e.g., 2024

    @Column(name = "prompt_tokens")
    private Integer promptTokens;

    @Column(name = "completion_tokens")
    private Integer completionTokens;

    @Column(name = "total_tokens")
    private Integer totalTokens;

    @Column(name = "ranking")
    private Integer ranking; // nullable, to be filled later

    @Column(name = "rationale", columnDefinition = "TEXT")
    private String rationale;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "market")
    private String market; // e.g., stockholm, baltic

}