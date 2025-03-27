package com.hardo.sentimentanalysis.chat;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/")
    public String chat() {
        return chatClient.prompt()
                .user("How many neutral statements there are? And by what company?")
                .call()
                .content();
    }

}
