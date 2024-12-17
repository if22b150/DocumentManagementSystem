package ta.technikumwien.dmsocr.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ta.technikumwien.dmsocr.persistence.DocumentIndex;
import ta.technikumwien.dmsocr.persistence.repository.DocumentIndexRepository;
import ta.technikumwien.dmsocr.service.dto.JobDTO;
import ta.technikumwien.dmsocr.service.dto.ResultDTO;
import ta.technikumwien.dmsocr.service.impl.MinioService;
import ta.technikumwien.dmsocr.service.impl.OcrService;

import java.io.InputStream;

@Service
public class OcrWorker {
    private final MinioService minioService;
    private final OcrService ocrService;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final DocumentIndexRepository documentIndexRepository;


    private static final Logger logger = LoggerFactory.getLogger(OcrWorker.class);


    @Value("${result.queue}")
    private String resultQueue;

    MessagePostProcessor messagePostProcessor = message -> {
        message.getMessageProperties().setHeader("__TypeId__", "OCRResultDTO");
        return message;
    };

    public OcrWorker(MinioService minioService, OcrService ocrService,
                     RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, DocumentIndexRepository documentIndexRepository) {
        this.minioService = minioService;
        this.ocrService = ocrService;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.documentIndexRepository = documentIndexRepository;
    }

    public void processOCRJob(JobDTO job) {
        try {
            // Fetch the document from MinIO
            InputStream pdfStream = minioService.getFile(job.getFileKey());
            // Perform OCR
            String recognizedText = ocrService.performOCR(pdfStream);

            // Save to ElasticSearch
            DocumentIndex documentIndex = DocumentIndex.builder()
                    .documentId(job.getDocumentId())
                    .content(recognizedText)
                    .build();
            documentIndexRepository.save(documentIndex);
            logger.info("Saved document to ElasticSearch with ID: {}", job.getDocumentId());

            // Send the result to RESULT_QUEUE
            ResultDTO result = new ResultDTO(job.getDocumentId(), recognizedText);
            rabbitTemplate.convertAndSend(resultQueue, result, messagePostProcessor);
            logger.info("Message sent to RabbitMQ: Document processed with ID: {}", result.getDocumentId());
            logger.info("Recognized text: " + recognizedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}