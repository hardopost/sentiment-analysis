package com.hardo.sentimentanalysis.analytics.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ChartsAndTopCompaniesSummariesDTO {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final CompanySummaryDTO base;
    @Getter
    @Setter
    private List<CategoryCountsDTO> categoryCounts;

    public ChartsAndTopCompaniesSummariesDTO(CompanySummaryDTO base) {
        this.base = base;
    }

    public Long getReportId() { return base.getReportId(); }
    public String getCompanyName() { return base.getCompanyName(); }
    public String getSector() { return base.getSector(); }
    public String getSummary() { return base.getSummary(); }

    public List<String> getPositiveDrivers() { return parseJsonArray(base.getPositiveDrivers()); }

    public List<String> getNegativeDrivers() {return parseJsonArray(base.getNegativeDrivers()); }

    public String getCapitalization() { return base.getCapitalization(); }
    public Integer getSectorRank() { return base.getSectorRank(); }
    public String getRankRationale() { return base.getRankRationale(); }
    public String getPeriod() { return base.getPeriod(); }
    public String getMarket() { return base.getMarket(); }


    private List<String> parseJsonArray(String json) {
        try {
            if (json == null || json.isBlank()) return List.of();
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            System.err.println("Failed to parse JSON array: " + json);
            return List.of();
        }
    }

}
