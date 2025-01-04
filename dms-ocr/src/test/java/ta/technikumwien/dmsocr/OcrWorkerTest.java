package ta.technikumwien.dmsocr;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import ta.technikumwien.dmsocr.persistence.repository.DocumentIndexRepository;
import ta.technikumwien.dmsocr.service.dto.JobDTO;
import ta.technikumwien.dmsocr.service.dto.ResultDTO;
import ta.technikumwien.dmsocr.service.impl.MinioService;
import ta.technikumwien.dmsocr.service.impl.OcrService;
import ta.technikumwien.dmsocr.service.impl.RabbitMQListenerService;
import ta.technikumwien.dmsocr.worker.OcrWorker;

import java.io.InputStream;
import java.util.Optional;

public class OcrWorkerTest {

    @Mock
    private InputStream mockPdfStream;
    
    @Mock
    private MinioService minioService;

    @Mock
    private OcrService ocrService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitMQListenerService rabbitMQListenerService;

    @Mock
    private JobDTO jobDTO;

    @Mock
    private DocumentIndexRepository documentIndexRepository;

    @Mock
    private InputStream inputStream;

    @Mock
    private OcrWorker ocrWorkerMock;
    private OcrWorker ocrWorker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ocrWorker = new OcrWorker(minioService, ocrService, rabbitTemplate, new ObjectMapper(), documentIndexRepository);
    }

    @Test
    void testProcessOCRJob() throws Exception {
        // Arrange
        JobDTO job = JobDTO.builder()
                .documentId(123L)
                .fileKey("file123")
                .build();

        String recognizedText = "Test recognized text";

        when(minioService.getFile(job.getFileKey())).thenReturn(inputStream);
        when(ocrService.performOCR(inputStream)).thenReturn(recognizedText);

        // Act
        ocrWorker.processOCRJob(job);

        // Assert
        verify(ocrService).performOCR(inputStream);
        verify(documentIndexRepository).save(any());
    }

    @Test
    void testProcessOCRJob_ExceptionHandling() throws Exception {
        // Arrange
        JobDTO job = JobDTO.builder()
                .documentId(123L)
                .fileKey("file123")
                .build();

        when(minioService.getFile(job.getFileKey())).thenThrow(new RuntimeException("MinIO error"));

        // Act
        ocrWorker.processOCRJob(job);

        // Assert
        verify(ocrService, never()).performOCR(any());
        verify(documentIndexRepository, never()).save(any());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(), Optional.ofNullable(any()));
    }

    @Test
    void testPerformOCR_Exception() throws Exception {
        // Arrange
        when(ocrService.performOCR(any())).thenThrow(new RuntimeException("OCR error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> ocrService.performOCR(mockPdfStream));
    }

    @Test
    void testReceiveMessage() throws Exception {
        // Arrange
        when(jobDTO.getDocumentId()).thenReturn(123L);

        // Act
        rabbitMQListenerService.receiveMessage(jobDTO);

        // Assert
        verify(ocrWorkerMock, times(1)).processOCRJob(jobDTO);
    }

    @Test
    void testReceiveMessage_Exception() throws Exception {
        // Arrange
        when(jobDTO.getDocumentId()).thenReturn(123L);
        doThrow(new RuntimeException("OCR processing error")).when(ocrWorkerMock).processOCRJob(jobDTO);

        // Act
        rabbitMQListenerService.receiveMessage(jobDTO);

        // Assert
        verify(ocrWorkerMock, times(1)).processOCRJob(jobDTO);
    }
}
