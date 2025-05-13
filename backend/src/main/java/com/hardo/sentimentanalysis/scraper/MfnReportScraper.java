package com.hardo.sentimentanalysis.scraper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class MfnReportScraper {

    private static final String MFN_BASE_URL = "https://mfn.se/all/s/nordic?limit=240&filter=(and(or(.properties.tags%40%3E%5B%22sub%3Areport%22%5D))(or(.properties.lang%3D%22en%22))(or(a.market_segment_ids%40%3E%5B13%5D)(a.market_segment_ids%40%3E%5B14%5D)(a.market_segment_ids%40%3E%5B15%5D)))&query";
    private final WebDriver driver;

    public MfnReportScraper(WebDriver driver) {
        this.driver = driver;
    }

    public String fetchAnnualReportPdf(String year, String companyName) {
        try {
            // 1. Navigate to MFN filtered page
            driver.get(MFN_BASE_URL);

            companyName = cleanCompanyName(companyName);

            // 2. Input the filter text in the free text box
            WebElement filterInput = driver.findElement(By.id("free-text-filter"));
            filterInput.clear();
            filterInput.sendKeys(companyName + " annual " + "report " + year);

            // 3. Pause briefly to allow filtering to apply
            Thread.sleep(1500); // Consider replacing with WebDriverWait if needed

            // 4. Get all visible short items
            List<WebElement> items = driver.findElements(By.cssSelector("div.short-item-wrapper"));
            //System.out.println("Found " + items.size() + " items (after filter)");

            // Prepare company name parts for matching
            List<String> keywords = Arrays.stream(companyName.toLowerCase().split("\\s+"))
                    .filter(word -> !word.matches("(?i)ab|group|plc|corp|ltd|as|oyj|asa|s\\.a\\.|inc|ag|publ|company|limited"))
                    .toList();

            for (WebElement item : items) {
                String titleText = item.getText().toLowerCase();

                // Check text contains report keywords and the year
                boolean isAnnualReport = titleText.contains("annual") && (titleText.contains("report") && titleText.contains(year));

                // Try to match company name from author element&&
                WebElement authorElement = item.findElement(By.cssSelector("span.compressed-author"));
                String authorText = authorElement.getText().toLowerCase();

                boolean companyMatch = keywords.stream().anyMatch(authorText::contains);

                if (isAnnualReport && companyMatch) {
                    List<WebElement> pdfLinks = item.findElements(By.cssSelector("a.attachment-icon[href$='.pdf']"));
                    if (!pdfLinks.isEmpty()) {
                        WebElement chosen = pdfLinks.get(0);
                        String href = chosen.getAttribute("href");
                        //System.out.println("✅ PDF found for " + companyName + ": " + href);
                        return href;
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Selenium failed: " + e.getMessage());
        }

        return null;
    }

    //helper to clean company names
    public String cleanCompanyName(String rawName) {
        if (rawName == null) return "";

        return rawName
                .replaceAll("(?i)\\b(ab|oy|oyj|as|asa|plc|inc|corp|corporation|ltd|limited|group|holding|publ|s\\.a\\.|s\\.p\\.a\\.|nv|ag|company)\\b", "")
                .replaceAll("[^\\p{L}\\p{N} ]", "") // remove punctuation
                .replaceAll("\\s+", " ")            // normalize whitespace
                .trim();
    }
}