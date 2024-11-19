package ta.technikumwien.dmsocr.service.impl;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

@Service
public class OcrService {
    public String performOCR(InputStream pdfStream) throws Exception {
        // Save InputStream to a temporary file
        File tempFile = File.createTempFile("ocr-", ".pdf");
        Files.copy(pdfStream, tempFile.toPath());

        // Perform OCR using Tesseract
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("/usr/share/tessdata"); // Path to Tesseract data
        return tesseract.doOCR(tempFile);
    }
}
