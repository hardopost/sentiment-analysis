package com.hardo.sentimentanalysis;

import com.hardo.sentimentanalysis.extraction.ReportDownloadService;
import com.hardo.sentimentanalysis.importing.ReportImportService;
import com.hardo.sentimentanalysis.processing.StatementProcessingService;
import com.hardo.sentimentanalysis.search.ReportLinkDiscoveryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.nio.file.Path;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/*@Bean
	CommandLineRunner runOnStartup(ReportDownloadService downloadService) {
		return args -> {
			downloadService.downloadAvailableReports();
		};
	}*/

	/*@Bean
	CommandLineRunner runOnStartup(ReportLinkDiscoveryService discoveryService) {
		return args -> {
			discoveryService.discoverAndSaveLinks();
		};
	}*/

	/*@Bean
	CommandLineRunner runOnStartup(ReportLinkDiscoveryService discoveryService) {
		return args -> {
			discoveryService.findMeDownloadLink();
		};
	}*/

	/*@Bean
	CommandLineRunner loadReportsFromCsv(ReportImportService reportImportService, ResourceLoader resourceLoader) {
		return args -> {
			Resource resource = resourceLoader.getResource("classpath:data/omx_stockholm_companies.csv");
			File csvFile = resource.getFile();
			if (csvFile.exists()) {
				reportImportService.importFromCsv(csvFile);
			} else {
				System.err.println("❌ CSV file not found: " + csvFile.getAbsolutePath());
			}
		};
	}*/

	/*@Bean
	CommandLineRunner run(StatementProcessingService statementProcessingService) {
		return args -> {
			try {
				statementProcessingService.processPdf("pdffile.pdf");
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
	@Bean
	CommandLineRunner run(StatementProcessingService service) {
		return args -> {
			Path reportsDir = Path.of("reports"); // This matches where your files are saved
			File[] pdfFiles = reportsDir.toFile().listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

			if (pdfFiles != null) {
				for (int i = 0; i < pdfFiles.length; i++) {
					File pdfFile = pdfFiles[i];
					String reportName = pdfFile.getName();
					System.out.printf("Processing: %s (%d/%d)%n", reportName, i + 1, pdfFiles.length);


					service.processPdf(pdfFile);

					// Add a delay between requests if needed
					// Thread.sleep(500); // 0.5 second pause (adjust if necessary)

				}
			}
		};
	}

	/*@Bean
	CommandLineRunner commandLineRunner(GoogleSearchService googleSearchService) {
		return args -> {

			String query = "Alleima 2024 annual sustainability report site:alleima.com filetype:pdf";
			String result = googleSearchService.search(query);
			System.out.println("🔍 Search Result: \n" + result);

		};
	}*/

}
