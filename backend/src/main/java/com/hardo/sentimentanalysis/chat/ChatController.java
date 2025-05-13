package com.hardo.sentimentanalysis.chat;

import com.hardo.sentimentanalysis.googlesearch.GoogleSearchTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatClient chatClient;
    private final GoogleSearchTool googleSearchTool;
    private final RagService ragService;

    public ChatController(ChatClient chatClient, GoogleSearchTool googleSearchTool, RagService ragService) {
        this.chatClient = chatClient;
        this.googleSearchTool = googleSearchTool;
        this.ragService = ragService;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequestDTO request) {
        System.out.println("Chat request: " + request.getMessage());

        // --- RAG Step 1: Retrieve Documents ---
        // Retrieve top N relevant documents (e.g., top 5)
        //List<Document> relevantDocuments = ragService.retrieveDocuments(request.getMessage(), 5);

        // --- RAG Step 2: Format Context ---
        //String context = ragService.formatDocuments(relevantDocuments);

        // --- RAG Step 3: Create Augmented Prompt ---
        // Define a system prompt template that instructs the LLM on how to use the context and tools
        String systemPromptTemplate = """
            You are a specialized financial assistant. Your primary task is to answer questions about companies' forward-looking statements using the provided context below.

            **Instructions:**
            1. Base your answer *strictly* on the information found in the 'Retrieved Context' section.
            2. If the context contains relevant information, synthesize it to answer the user's question.
            3. If the context does *not* contain information relevant to the question, clearly state that the provided documents do not have the answer.
            4. *Only if* the context is insufficient, you may use the 'googleSearchToolFunction' tool to find external information. Do not use the tool if the context is sufficient.
            5. Quote or reference the source statement(s) from the context when possible.

            **Retrieved Context (Forward-Looking Statements from 2024 Annual Reports):**
            --- START CONTEXT ---
            {CONTEXT_PLACEHOLDER}
            --- END CONTEXT ---

            Now, please answer the user's question based on these instructions.
            """;


        // Inject the retrieved context into the template
        //String finalSystemPrompt = systemPromptTemplate.replace("{CONTEXT_PLACEHOLDER}" );// context

        // Create the messages
        //SystemMessage systemMessage = new SystemMessage(systemPromptTemplate);
        //UserMessage userMessage = new UserMessage(request.getMessage());

        var userMessage = new UserMessage(request.getMessage());
        var systemMessage = new SystemMessage("You are a helpful assistant whose main task is to answer user questions about companies forward looking statements. Use the extra content provided by RAG to answer. Don't mention the RAG process to the user. If you don't know the answer, say 'I don't know'.");
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        String conversationId = "1";
        return chatClient.prompt(prompt)
                .tools(googleSearchTool)
                .advisors(a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId))
                .call()
                .content();
    }
}
