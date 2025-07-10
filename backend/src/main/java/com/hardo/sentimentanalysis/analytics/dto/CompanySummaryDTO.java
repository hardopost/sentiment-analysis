package com.hardo.sentimentanalysis.analytics.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CompanySummaryDTO {
    Long reportId; //Report ID, basically company ID. Report = Company in this context
    String companyName;
    String sector; // "Technology"
    String summary; //Company outlook
    String positiveDrivers;
    String negativeDrivers;
    String capitalization; //Small, Medium, Large
    Integer sectorRank; //Rank of the company in its sector 1, 2, 3
    String rankRationale; //Rationale for the rank
    String period; //Period of the report
    String market; //Market of the report "stockholm", "baltic"
}
