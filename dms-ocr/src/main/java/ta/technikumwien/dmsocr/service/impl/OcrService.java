package ta.technikumwien.dmsocr.service.impl;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Service
public class OcrService {
    public String performOCR(InputStream pdfStream) throws Exception {
        // Perform OCR using Tesseract
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata/");

        File tempFile = File.createTempFile("ocr-", ".pdf");
        try {
            Files.copy(pdfStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Perform OCR
            return tesseract.doOCR(tempFile);

        } finally {
            // Clean up temporary file
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}
