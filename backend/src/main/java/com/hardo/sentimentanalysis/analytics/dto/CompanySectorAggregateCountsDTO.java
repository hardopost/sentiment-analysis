package com.hardo.sentimentanalysis.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompanySectorAggregateCountsDTO {
    private Map<String, List<CategoryCountsDTO>> companies;
    private Map<String, List<CategoryCountsDTO>> sectors;

}
