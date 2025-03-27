package com.hardo.sentimentanalysis.processing;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "statement")
public class Statement {

    @Id
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;  // 'company' or 'sector'

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String industry;

    @Column(nullable = false)
    private String supersector;

    @Column(nullable = false)
    private String sector;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String sentiment; // 'positive', 'negative', 'neutral'

    @Column(nullable = false)
    private String period;  // Example: "2024", "Q1-2025"

    public enum Type {
        company, sector
    }

}

