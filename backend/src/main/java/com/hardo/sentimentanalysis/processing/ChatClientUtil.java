package com.hardo.sentimentanalysis.processing;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.ParameterizedTypeReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatClientUtil {

    private static final Logger logger = LoggerFactory.getLogger(ChatClientUtil.class);
    private static final int GEMINI_25_PRO_EXP_03_25 = 65536;

    public static <T> LlmResult<T> sendPromptWithTokenInfo(
            ChatClient chatClient,
            Prompt prompt,
            ParameterizedTypeReference<T> responseType
    ) {
        var responseEntity = chatClient
                .prompt(prompt)
                .call()
                .responseEntity(responseType);

        ChatResponse chatResponse = responseEntity.getResponse();
        if (chatResponse == null) {
            throw new IllegalStateException("Chat response is null");
        }

        var usage = chatResponse.getMetadata().getUsage();
        int promptTokens = usage.getPromptTokens();
        int completionTokens = usage.getCompletionTokens();
        int totalTokens = usage.getTotalTokens();

        if (completionTokens >= GEMINI_25_PRO_EXP_03_25 - 100) {
            logger.warn("Completion tokens ({}) are near Gemini Flash limit ({}). Response may be truncated!",
                    completionTokens, GEMINI_25_PRO_EXP_03_25);
        }

        logger.info("Prompt tokens: {}, Completion tokens: {}, Total: {}",
                promptTokens, completionTokens, totalTokens);

        return new LlmResult<>(responseEntity.getEntity(), promptTokens, completionTokens, totalTokens);
    }
}