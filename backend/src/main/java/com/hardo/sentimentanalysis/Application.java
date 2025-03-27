package com.hardo.sentimentanalysis;

import com.hardo.sentimentanalysis.processing.StatementProcessingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner run(StatementProcessingService statementProcessingService) {
		return args -> {
			try {
				statementProcessingService.processPdf("02041910.pdf");
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}
}
