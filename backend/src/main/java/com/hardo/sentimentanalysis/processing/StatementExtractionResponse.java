package com.hardo.sentimentanalysis.processing;

import java.util.List;

public record StatementExtractionResponse(
        List<StatementDTO> statements,
        OutlookSummaryDTO summary
) {}