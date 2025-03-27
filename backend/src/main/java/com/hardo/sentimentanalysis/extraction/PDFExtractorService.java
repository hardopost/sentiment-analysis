package com.hardo.sentimentanalysis.extraction;


import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.hibernate.annotations.ValueGenerationType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;

@Service
public class PDFExtractorService {

    //private static final String PDF_DIRECTORY = "src/main/pdfs/";  // Where PDFs are stored
    @Value("classpath:/pdfs/AKO_2023-2024.pdf")
    private Resource pdfFile;

    public String extractTextFromPDF() throws IOException {


        //File file = new File(PDF_DIRECTORY + fileName);
        //if (!file.exists()) throw new IOException("PDF file not found: " + file.getAbsolutePath());

        try (PDDocument document = Loader.loadPDF(pdfFile.getFile())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }
}