package com.hardo.sentimentanalysis.chat;

import com.hardo.sentimentanalysis.search.GoogleSearchTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChatController {

    private final ChatClient chatClient;
    private final GoogleSearchTool googleSearchTool;

    public ChatController(ChatClient chatClient, GoogleSearchTool googleSearchTool) {
        this.chatClient = chatClient;
        this.googleSearchTool = googleSearchTool;
    }

    @GetMapping("/")
    public String chat() {
        var userMessage = new UserMessage("Find me a download link for Annual and Sustainability report of the company Alleima for 2024. " +
        "The report should be in PDF format and in English language, if it is not available in English, it can be in Swedish. " +
                "If Annual and Sustainability report is not available, find me just the 2024 Annual report link.");
        var systemMessage = new SystemMessage("You must use tools available to search annual and sustainability report for company the user asks" +
                "For example, if the user asks for company Alleima 2024 sustainability report report for 2024, you should search for 'Alleima 2024 annual and sustainability report site:alleima.com filetype:pdf' ");
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        return chatClient
                .prompt(prompt)
                .tools(googleSearchTool)
                .call()
                .content();

    }

}
