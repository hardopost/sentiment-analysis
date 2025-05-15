package com.hardo.sentimentanalysis.analytics.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AllSectorSummariesAndChartsDTO {

    List<SectorSummariesDTO> sectors;

}
