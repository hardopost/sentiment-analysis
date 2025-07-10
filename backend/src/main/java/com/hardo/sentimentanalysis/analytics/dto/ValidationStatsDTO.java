package com.hardo.sentimentanalysis.analytics.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ValidationStatsDTO {
    public Long matchFoundTrue;
    public Long matchFoundFalse;
    public Long typeValidTrue;
    public Long typeValidFalse;
    public Long categoryValidTrue;
    public Long categoryValidFalse;
    public Long sentimentValidTrue;
    public Long sentimentValidFalse;
    public Long sectorValidTrue;
    public Long sectorValidFalse;
    public Long esgYesFinancial;
    public Long esgYesNonfinancial;
    public Long esgNo;
}