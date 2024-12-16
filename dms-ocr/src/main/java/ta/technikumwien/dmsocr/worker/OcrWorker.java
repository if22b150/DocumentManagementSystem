package ta.technikumwien.dmsocr.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ta.technikumwien.dmsocr.service.dto.JobDTO;
import ta.technikumwien.dmsocr.service.dto.ResultDTO;
import ta.technikumwien.dmsocr.service.impl.ElasticsearchService;
import ta.technikumwien.dmsocr.service.impl.MinioService;
import ta.technikumwien.dmsocr.service.impl.OcrService;

import java.io.InputStream;

@Service
public class OcrWorker {
    private final MinioService minioService;
    private final OcrService ocrService;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    private final ElasticsearchService elasticsearchService;

    private static final Logger logger = LoggerFactory.getLogger(OcrWorker.class);


    @Value("${result.queue}")
    private String resultQueue;

    MessagePostProcessor messagePostProcessor = message -> {
        message.getMessageProperties().setHeader("__TypeId__", "OCRResultDTO");
        return message;
    };

    public OcrWorker(MinioService minioService, OcrService ocrService,
                     RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, ElasticsearchService elasticsearchService) {
        this.minioService = minioService;
        this.ocrService = ocrService;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.elasticsearchService = elasticsearchService;
    }

    public void processOCRJob(JobDTO job) {
        try {
            // Fetch the document from MinIO
            InputStream pdfStream = minioService.getFile(job.getFileKey());
            // Perform OCR
            String recognizedText = ocrService.performOCR(pdfStream);

            // Save in elasticsearch
            ResultDTO result = new ResultDTO(job.getDocumentId(), recognizedText);
            elasticsearchService.init();
            //elasticsearchService.saveDocument(result);

            // Send the result to RESULT_QUEUE
            rabbitTemplate.convertAndSend(resultQueue, result, messagePostProcessor);
            logger.info("Message sent to RabbitMQ: Document processed with ID: {}", result.getDocumentId());
            logger.info("Recognized text: " + recognizedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}