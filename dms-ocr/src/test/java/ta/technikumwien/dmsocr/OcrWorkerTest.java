package ta.technikumwien.dmsocr;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import ta.technikumwien.dmsocr.persistence.repository.DocumentIndexRepository;
import ta.technikumwien.dmsocr.service.dto.JobDTO;
import ta.technikumwien.dmsocr.service.dto.ResultDTO;
import ta.technikumwien.dmsocr.service.impl.MinioService;
import ta.technikumwien.dmsocr.service.impl.OcrService;
import ta.technikumwien.dmsocr.worker.OcrWorker;

import java.io.InputStream;
import java.util.Optional;

public class OcrWorkerTest {

    @Mock
    private MinioService minioService;

    @Mock
    private OcrService ocrService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private DocumentIndexRepository documentIndexRepository;

    @Mock
    private InputStream inputStream;

    private OcrWorker ocrWorker;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ocrWorker = new OcrWorker(minioService, ocrService, rabbitTemplate, new ObjectMapper(), documentIndexRepository);
    }

    @Test
    public void testProcessOCRJob() throws Exception {
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
        // Verify that the OCR service was called
        verify(ocrService).performOCR(inputStream);

        // Verify that the document index was saved to the repository
        verify(documentIndexRepository).save(any());
    }

    @Test
    public void testProcessOCRJob_ExceptionHandling() throws Exception {
        // Arrange
        JobDTO job = JobDTO.builder()
                .documentId(123L)
                .fileKey("file123")
                .build();

        when(minioService.getFile(job.getFileKey())).thenThrow(new RuntimeException("MinIO error"));

        // Act
        ocrWorker.processOCRJob(job);

        // Assert
        // Verify no further actions were performed
        verify(ocrService, never()).performOCR(any());
        verify(documentIndexRepository, never()).save(any());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(), Optional.ofNullable(any()));
    }
}
