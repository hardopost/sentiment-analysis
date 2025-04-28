package com.hardo.sentimentanalysis.extraction;

import com.hardo.sentimentanalysis.processing.Report;
import com.hardo.sentimentanalysis.processing.ReportRepository;
import org.asynchttpclient.*;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

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

        List<Future<Void>> downloadFutures = new ArrayList<>();

        for (Report report : reports) {
            String fileUrl = report.getDownloadLink();
            String safeFileName = generateFileName(report);
            Path outputPath = outputDir.resolve(safeFileName);

            Future<Void> future =
                    httpClient.prepareGet(fileUrl)
                            .setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                                    "(KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                            .execute(new AsyncCompletionHandler<>() {

                private FileOutputStream stream;

                @Override
                public State onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
                    if (stream == null) {
                        stream = new FileOutputStream(outputPath.toFile());
                    }
                    int written = stream.getChannel().write(bodyPart.getBodyByteBuffer());
                    //System.out.printf("⬇️  Writing %d bytes for %s%n", written, report.getCompanyName());
                    return State.CONTINUE;
                }

                @Override
                public Void onCompleted(Response response) throws Exception {
                    if (stream != null) {
                        stream.flush();
                        stream.close();
                    }

                    long fileSize = Files.size(outputPath);
                    if (fileSize < 10240) { // less than 1KB
                        System.out.println("⚠️ Skipped (too small): " + safeFileName + " (" + fileSize + " bytes)");
                        // optionally delete small file
                        Files.deleteIfExists(outputPath);
                        return null;
                    }

                    report.setDownloaded(true);
                    reportRepository.save(report);
                    System.out.println("✅ Downloaded: " + safeFileName);
                    return null;
                }

                @Override
                public void onThrowable(Throwable t) {
                    System.err.println("❌ Failed to download for " + report.getCompanyName() + ": " + t.getMessage());
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException ignored) {}
                    }
                }
            });

            downloadFutures.add(future);
        }

        for (Future<Void> future : downloadFutures) {
            try {
                future.get(); // Wait for all downloads to complete
            } catch (Exception e) {
                System.err.println("❌ Error waiting for download to complete: " + e.getMessage());
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
