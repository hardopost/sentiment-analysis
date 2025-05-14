package com.hardo.sentimentanalysis.analytics.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SectorSummaryAndChartDTO {
    String sector; // "Technology"
    String summary;
    String tone;
    String toneRationale;
    String ranking;
    String rankingRationale;
    List<String> categoriesCounts;

}
