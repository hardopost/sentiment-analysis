package com.hardo.sentimentanalysis;

import com.hardo.sentimentanalysis.processing.StatementProcessingService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/*@Bean
	CommandLineRunner run(StatementProcessingService statementProcessingService) {
		return args -> {
			try {
				statementProcessingService.processPdf("02041910.pdf");
			} catch (Exception e) {
				e.printStackTrace();
			}
		};*/

	/*@Bean
	CommandLineRunner commandLineRunner(ChatClient.Builder builder) {
		return args -> {
			var chat = builder.build();

			ChatResponse response = chat.prompt("Tell me an interesting fact about Google")
					.call()
					.chatResponse();

			System.out.println(response);

		};
	}*/
}
