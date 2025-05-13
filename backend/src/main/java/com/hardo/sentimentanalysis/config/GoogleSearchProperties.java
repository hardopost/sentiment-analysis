package com.hardo.sentimentanalysis.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "google.search")
public class GoogleSearchProperties {
    private String apiKey;
    private String engineId;

}