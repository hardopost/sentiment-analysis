package com.hardo.sentimentanalysis.util;


public record LlmResult<T>(
        T output,
        int promptTokens,
        int completionTokens,
        int totalTokens
) {}

