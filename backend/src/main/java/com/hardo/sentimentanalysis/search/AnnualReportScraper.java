package com.hardo.sentimentanalysis.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AnnualReportScraper {

    /**
     * Looks up the correct company page URL from a fuzzy company name.
     * @param companyName e.g. "Atlas Copco"
     * @return Full URL to the company's page, or null if not found.
     */

    public String resolveCompanyUrl(String companyName) {
        try {
            String query = companyName.trim().replace(" ", "+");
            String searchUrl = "https://www.annualreports.com/Companies?search=" + query;

            Document doc = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();

            // Get the first search result
            Element link = doc.selectFirst("span.companyName > a");

            if (link != null) {
                return link.absUrl("href"); // full absolute link
            } else {
                System.out.println("❌ No company match found for: " + companyName);
            }

        } catch (Exception e) {
            System.err.println("⚠️ Error during company search: " + e.getMessage());
        }

        return null;
    }

    public String extractReportForYear(String companyPageUrl, String year) {
        try {
            Document doc = Jsoup.connect(companyPageUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();

            // 1. Try MOST RECENT block
            Element mostRecentBlock = doc.selectFirst("div.most_recent_block");
            if (mostRecentBlock != null) {
                Element yearText = mostRecentBlock.selectFirst("span.bold_txt");
                if (yearText != null && yearText.text().contains(year + " Annual Report")) {
                    Element pdfLink = mostRecentBlock.selectFirst("div.view_btn a");
                    if (pdfLink != null) {
                        return pdfLink.absUrl("href");
                    }
                }
            }

            // 2. Try ARCHIVED block
            Elements archivedItems = doc.select("div.archived_report_block li");
            for (Element item : archivedItems) {
                Element heading = item.selectFirst("div.text_block span.heading");
                if (heading != null && heading.text().contains(year + " Annual Report")) {
                    Element downloadLink = item.selectFirst("span.download a");
                    if (downloadLink != null) {
                        return downloadLink.absUrl("href");
                    }
                }
            }

            System.out.println("❌ Annual Report for year " + year + " not found.");
        } catch (Exception e) {
            System.err.println("⚠️ Error: " + e.getMessage());
        }

        return null;
    }
}


