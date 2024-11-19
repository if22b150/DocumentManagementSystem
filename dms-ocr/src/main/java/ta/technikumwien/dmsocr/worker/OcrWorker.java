package ta.technikumwien.dmsocr.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ta.technikumwien.dmsocr.config.RabbitMQConfig;
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

    private static final Logger logger = LoggerFactory.getLogger(OcrWorker.class);


    @Value("${result.queue}")
    private String resultQueue;

    public OcrWorker(MinioService minioService, OcrService ocrService,
                     RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.minioService = minioService;
        this.ocrService = ocrService;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.OCR_QUEUE)
    public void processOCRJob(JobDTO job) {
        try {
            System.out.println("Received OCR job: " + job);
            logger.info("Document received", job);
            // Parse the OCR job message
//            JobDTO job = objectMapper.readValue(message, JobDTO.class);

            // Fetch the document from MinIO
            InputStream pdfStream = minioService.getFile(job.getFileKey());

            // Perform OCR
            String recognizedText = ocrService.performOCR(pdfStream);

            // Send the result to RESULT_QUEUE
            ResultDTO result = new ResultDTO(job.getDocumentId(), recognizedText);
            rabbitTemplate.convertAndSend(resultQueue, objectMapper.writeValueAsString(result));

            System.out.println("OCR processing completed for document: " + job.getDocumentId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}