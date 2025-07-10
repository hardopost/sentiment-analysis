package com.hardo.sentimentanalysis;

import com.hardo.sentimentanalysis.processing.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
	public CommandLineRunner runner(ReportLinkFinderService resolver) {
		return args -> {
			resolver.resolveMissingReportLinks("2024");
		};
	}*/


	/*@Bean
	CommandLineRunner loadReportsFromCsv(NameImportService reportImportService, ResourceLoader resourceLoader) {
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
	CommandLineRunner run(StatementProcessingService service) {
		return args -> {
			Path reportsDir = Path.of("reports"); // This matches where your files are saved
			File[] pdfFiles = reportsDir.toFile().listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

			if (pdfFiles != null) {
				for (int i = 0; i < pdfFiles.length; i++) {
					File pdfFile = pdfFiles[i];
					String reportName = pdfFile.getName();
					System.out.printf("Processing: %s (%d/%d)%n", reportName, i + 1, pdfFiles.length);


					service.processAnnualReportPdf(pdfFile);

					// Add a delay between requests if needed
					// Thread.sleep(500); // 0.5 second pause (adjust if necessary)

				}
			}
		};
	}*/

	/*@Bean
	CommandLineRunner run(StatementValidationService service) {
		return args -> {
			Path reportsDir = Path.of("reports"); // This matches where your files are saved
			File[] pdfFiles = reportsDir.toFile().listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

			if (pdfFiles != null) {
				for (int i = 0; i < pdfFiles.length; i++) {
					File pdfFile = pdfFiles[i];
					String reportName = pdfFile.getName();
					System.out.printf("Processing: %s (%d/%d)%n", reportName, i + 1, pdfFiles.length);


					service.validateStatements(pdfFile);

					// Add a delay between requests if needed
					// Thread.sleep(500); // 0.5 second pause (adjust if necessary)

				}
			}
		};
	}*/


	//For creating sector summaries
	/*@Bean
	CommandLineRunner runSectorSummaryService(SectorSummaryService service) {
		return args -> {
			service.generateSectorSummary("2024", "baltic");
		};
	}*/

	//For creating sector rankings
	/*@Bean
	CommandLineRunner runSectorRankingService(SectorRankingService service) {
		return args -> {
			service.rankSectors("2024", "baltic"); //change to "stockholm" if needed then change amount of sectors from 7 to 11 in the sector-ranking-prompt also
		};
	}*/

	//For creating company rankings in sectors
	/*@Bean
	CommandLineRunner runCompanyRankingService(CompanyRankingService service) {
		return args -> {
			service.rankCompaniesInSectors("2024", "baltic");
		};
	}*/

}
