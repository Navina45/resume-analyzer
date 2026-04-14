package resume_analyzer_backend;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public class PdfUtils {

    public static String extractText(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            if (text == null || text.trim().isEmpty()) {
                return "No readable text found in the PDF.";
            }

            return text.trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error extracting text from PDF: " + e.getMessage();
        }
    }
}