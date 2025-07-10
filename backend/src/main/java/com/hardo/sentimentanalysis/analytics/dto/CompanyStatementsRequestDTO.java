package com.hardo.sentimentanalysis.analytics.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CompanyStatementsRequestDTO {
    private String type;
    private String companyName;
    private String category;
    private String sentiment;
    private String market;
}
