package com.hardo.sentimentanalysis.extraction;


import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;

@Service
public class PDFExtractorService {

    public String extractTextFromPDF(File pdfFile) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String rawText = pdfStripper.getText(document);
            return sanitizeText(rawText);
        }
    }

    private String sanitizeText(String input) {
        if (input == null) return null;
        // Remove all control characters except \n, \r, and \t
        return input.replaceAll("[\\x00-\\x1F&&[^\\n\\r\\t]]", "");
    }

}