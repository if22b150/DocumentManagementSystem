package ta.technikumwien.dmsocr.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ta.technikumwien.dmsocr.service.dto.JobDTO;
import ta.technikumwien.dmsocr.worker.OcrWorker;

@Service
@RequiredArgsConstructor
public class RabbitMQListenerService {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQListenerService.class);
    private final OcrWorker ocrWorker;

    public void receiveMessage(JobDTO jobDTO) {
        logger.info("Received message from RabbitMQ: " + jobDTO.getDocumentId());
        try {
            // Call processOCRJob method from OcrWorker
            ocrWorker.processOCRJob(jobDTO);
        } catch (Exception e) {
            logger.error("Error processing OCR job for document: " + jobDTO.getDocumentId(), e);
        }
    }
}
