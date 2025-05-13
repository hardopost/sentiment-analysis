package com.hardo.sentimentanalysis.scraper;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class UtilTools {

    @Tool(description = "Provides the current date and time as a JSON object.")
    public Map<String, String> getCurrentDateTime() {
        String now = LocalDateTime.now().toString();
        System.out.println("Tool called, returning structured datetime: " + now);

        return Map.of("datetime", now);
    }

    @Tool(description = "Set a user alarm for the given time, provided in ISO-8601 format.")
    public Map<String, String> setAlarm(String time) {
        LocalDateTime alarmTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
        System.out.println("Alarm set for " + alarmTime);

        return Map.of( "alarmTime", alarmTime.toString());
    }
}
