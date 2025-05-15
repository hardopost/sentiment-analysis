package com.hardo.sentimentanalysis.analytics.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ChartsAndTopCompaniesSummariesDTO {

    private final CompanySummariesDTO base;
    @Getter
    @Setter
    private List<CategoryCountsDTO> categoryCounts; // mutable list


    public ChartsAndTopCompaniesSummariesDTO(CompanySummariesDTO base) {
        this.base = base;
    }
    public Long getReportId() { return base.getReportId(); }
    public String getCompanyName() { return base.getCompanyName(); }
    public String getSector() { return base.getSector(); }
    public String getSummary() { return base.getSummary(); }
    public String getCapitalization() { return base.getCapitalization(); }
    public Integer getSectorRank() { return base.getSectorRank(); }
    public String getRankRationale() { return base.getRankRationale(); }
    public String getPeriod() { return base.getPeriod(); }

}
