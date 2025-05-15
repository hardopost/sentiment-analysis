package com.hardo.sentimentanalysis.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.catalina.LifecycleState;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompanySectorAggregateCountsDTO {
    private List<CategoryCountsDTO> companies;
    private List<CategoryCountsDTO> sectors;

}
