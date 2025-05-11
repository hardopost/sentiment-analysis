package com.hardo.sentimentanalysis.processing;


public record LlmResult<T>(
        T output,
        int promptTokens,
        int completionTokens,
        int totalTokens
) {}

