package com.hardo.sentimentanalysis.search;

import java.util.List;

import com.hardo.sentimentanalysis.domain.Report;
import com.hardo.sentimentanalysis.domain.ReportRepository;
import org.springframework.stereotype.Service;

@Service
public class ReportLinkFinderService {

    private final ReportRepository reportRepository;
    private final AnnualReportScraper scraper;
    private final MfnReportFinder mfnReportFinder;

    public ReportLinkFinderService(ReportRepository reportRepository,
                                   AnnualReportScraper scraper,
                                   MfnReportFinder mfnReportFinder) {
        this.reportRepository = reportRepository;
        this.scraper = scraper;
        this.mfnReportFinder = mfnReportFinder;
    }

    public void resolveMissingReportLinks(String year) {
        List<Report> reports = reportRepository.findAll();
        //List<Report> reports = reportRepository.findAllById(List.of(111L, 112L, 113L, 114L, 115L));

        for (Report report : reports) {
            if (report.getDownloadLink() != null) continue;

            String pdfLink = null;
            String source = null;

            // 1. Search from MFN
            if (pdfLink == null) {
                pdfLink = mfnReportFinder.fetchAnnualReportPdf(year, report.getCompanyName());
                if (pdfLink != null) source = "MFN.se";
            }

            // 2. Fallback to search AnnualReports.com
            /*String companyUrl = scraper.resolveCompanyUrl(report.getCompanyName());
            if (companyUrl == null) {
                pdfLink = scraper.extractReportForYear(companyUrl, year);
                if (pdfLink != null) source = "AnnualReports.com";
            }*/


            // 3. Save if link found
            if (pdfLink != null) {
                report.setDownloadLink(pdfLink);
                reportRepository.save(report);
                System.out.printf("✅ [%s] Saved: %s → %s%n", source, report.getCompanyName(), pdfLink);
            } else {
                System.out.println("❌ No link found for: " + report.getCompanyName());
            }
        }
    }
}

