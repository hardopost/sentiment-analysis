package com.hardo.sentimentanalysis.analytics.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class StatementDTO {
    private String companyName;
    private String content;
}
