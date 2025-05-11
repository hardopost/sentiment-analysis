package com.hardo.sentimentanalysis.extraction;

import com.hardo.sentimentanalysis.domain.Report;
import com.hardo.sentimentanalysis.domain.ReportRepository;
import org.asynchttpclient.*;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
public class ReportDownloadService {

    private final ReportRepository reportRepository;
    private final AsyncHttpClient httpClient;

    public ReportDownloadService(ReportRepository reportRepository, AsyncHttpClient httpClient) {
        this.reportRepository = reportRepository;
        this.httpClient = httpClient;
    }

    public void downloadAvailableReports() {
        List<Report> reports = reportRepository.findAll().stream()
                .filter(r -> r.getDownloadLink() != null && !r.isDownloaded())
                .toList();

        Path outputDir = Path.of("reports");
        try {
            Files.createDirectories(outputDir);
        } catch (IOException e) {
            throw new RuntimeException("❌ Failed to create output directory", e);
        }

        for (Report report : reports) {
            try {
                String fileUrl = report.getDownloadLink();
                String safeFileName = generateFileName(report);
                Path outputPath = outputDir.resolve(safeFileName);

                System.out.printf("⬇️ Downloading: %s → %s%n", report.getCompanyName(), fileUrl);

                byte[] fileBytes = httpClient.prepareGet(fileUrl)
                        .setHeader("User-Agent", "Mozilla/5.0")
                        .execute()
                        .toCompletableFuture()
                        .get(120, TimeUnit.SECONDS)
                        .getResponseBodyAsBytes();

                Files.write(outputPath, fileBytes);

                if (fileBytes.length < 10240) {
                    System.out.println("⚠️ Skipped (too small): " + safeFileName + " (" + fileBytes.length + " bytes)");
                    Files.deleteIfExists(outputPath);
                    continue;
                }

                report.setDownloaded(true);
                reportRepository.save(report);
                System.out.println("✅ Downloaded: " + safeFileName);

                Thread.sleep(500); // Optional: rate limiting

            } catch (Exception e) {
                System.err.println("❌ Failed to download for " + report.getCompanyName() + ": " + e.getMessage());
            }
        }
    }

    private String generateFileName(Report report) {
        String cleanName = String.format("%d_%s_%s",
                report.getId(),
                report.getCompanyName().replaceAll("[^a-zA-Z0-9\\-_]", "_"),
                report.getPeriod() != null ? report.getPeriod() : "unknown"
        );
        return cleanName + ".pdf";
    }
}