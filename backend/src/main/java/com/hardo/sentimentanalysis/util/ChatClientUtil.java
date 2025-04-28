package com.hardo.sentimentanalysis.util;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.ParameterizedTypeReference;

public class ChatClientUtil {

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

        T output = responseEntity.getEntity();
        ChatResponse chatResponse = responseEntity.getResponse();
        if (chatResponse == null) {
            throw new IllegalStateException("Chat response is null");
        }
        int promptTokens = chatResponse.getMetadata().getUsage().getPromptTokens();
        int completionTokens = chatResponse.getMetadata().getUsage().getCompletionTokens();
        int totalTokens = chatResponse.getMetadata().getUsage().getTotalTokens();

        if (completionTokens >= GEMINI_25_PRO_EXP_03_25 - 100) {
            System.out.printf("⚠️ Completion tokens (%d) are near Gemini Flash limit (%d). Response may be truncated!%n",
                    completionTokens, GEMINI_25_PRO_EXP_03_25);
        }

        System.out.printf("✅ Prompt tokens: %d, Completion tokens: %d, Total: %d%n",
                promptTokens, completionTokens, totalTokens);

        return new LlmResult<T>(output, promptTokens, completionTokens, totalTokens);
    }
}