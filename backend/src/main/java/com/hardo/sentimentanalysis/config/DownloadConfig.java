package com.hardo.sentimentanalysis.config;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.Dsl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DownloadConfig {

    /*@Bean
    public AsyncHttpClient asyncHttpClient() {
        AsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder()
                .setFollowRedirect(true) // explicitly follow redirects
                .build();

        return Dsl.asyncHttpClient(config);
    }*/

    @Bean
public AsyncHttpClient asyncHttpClient() {
    DefaultAsyncHttpClientConfig.Builder clientBuilder = Dsl.config()
        .setConnectTimeout(15000)             // 15 sec to connect
        .setRequestTimeout(120000)            // 120 sec to complete request
        .setReadTimeout(120000)              // 120 sec read timeout
        .setFollowRedirect(true); // explicitly follow redirects

    return Dsl.asyncHttpClient(clientBuilder);
}
}
