package com.hardo.sentimentanalysis.analytics.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SectorStatementsRequestDTO {
    private String type;
    private String sector;
    private String category;
    private String sentiment;
    private String market;
}
