package com.hardo.sentimentanalysis.analytics;

import com.hardo.sentimentanalysis.analytics.dto.SentimentDataDTO;
import com.hardo.sentimentanalysis.analytics.dto.SentimentDataSummaryDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/sentiments")
    public SentimentDataSummaryDTO getSentimentsData() {
        return analyticsService.getSentimentsData();
    }

}
