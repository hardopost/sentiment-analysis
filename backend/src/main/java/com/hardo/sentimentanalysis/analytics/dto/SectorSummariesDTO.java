package com.hardo.sentimentanalysis.analytics.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SectorSummariesDTO {
    String sector; // "Technology"
    String summary;
    String sentiment;
    String toneRationale;
    Integer ranking;
    String rankingRationale;
}
