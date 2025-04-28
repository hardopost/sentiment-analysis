package com.hardo.sentimentanalysis.importing;

import com.hardo.sentimentanalysis.processing.Report;
import com.hardo.sentimentanalysis.processing.ReportRepository;
import org.springframework.stereotype.Service;
import org.apache.commons.csv.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Service
public class ReportImportService {

    private final ReportRepository reportRepository;

    public ReportImportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public void importFromCsv(File csvFile) {
        try (Reader reader = new InputStreamReader(new FileInputStream(csvFile), StandardCharsets.UTF_8)) {
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader("Company Name", "Sector", "Capitalization")
                    .setSkipHeaderRecord(true)
                    .get();

            try (CSVParser parser = CSVParser.parse(reader, format)) {
                for (CSVRecord record : parser) {
                    Report report = new Report();
                    report.setCompanyName(record.get("Company Name"));
                    report.setSector(record.get("Sector"));
                    report.setCapitalization(record.get("Capitalization"));
                    report.setPeriod("2024");
                    reportRepository.save(report);
                }
            }

            System.out.println("✅ Report data imported successfully.");
        } catch (IOException e) {
            throw new RuntimeException("Failed to import report data from CSV", e);
        }
    }
}