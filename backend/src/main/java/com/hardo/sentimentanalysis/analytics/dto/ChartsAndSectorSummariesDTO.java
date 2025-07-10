package com.hardo.sentimentanalysis.analytics.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ChartsAndSectorSummariesDTO {

    private final SectorSummariesDTO base;
    // Custom getter/setter
    @Getter
    @Setter
    private List<CategoryCountsDTO> categoryCounts; // mutable list

    public ChartsAndSectorSummariesDTO(SectorSummariesDTO base) {
        this.base = base;
    }

    // Delegate methods to base
    public String getSector() { return base.getSector(); }
    public String getSummary() { return base.getSummary(); }
    public String getSentiment() { return base.getSentiment(); }
    public String getToneRationale() { return base.getToneRationale(); }
    public Integer getRanking() { return base.getRanking(); }
    public String getRankingRationale() { return base.getRankingRationale(); }

}