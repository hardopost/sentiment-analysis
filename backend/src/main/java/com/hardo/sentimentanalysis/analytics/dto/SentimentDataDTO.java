package com.hardo.sentimentanalysis.analytics.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SentimentDataDTO {

    private String category;
    private long negativeCount;
    private long neutralCount;
    private long positiveCount;

}
