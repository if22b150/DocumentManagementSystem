package ta.technikumwien.dmsocr;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import at.technikumwien.dmsbackend.exception.DocumentUploadException;
import at.technikumwien.dmsbackend.persistence.entity.DocumentEntity;
import at.technikumwien.dmsbackend.persistence.repository.DocumentIndexRepository;
import at.technikumwien.dmsbackend.persistence.repository.DocumentRepository;
import at.technikumwien.dmsbackend.service.dto.DocumentDTO;
import at.technikumwien.dmsbackend.service.dto.OCRJobDTO;
import at.technikumwien.dmsbackend.service.impl.DocumentServiceImpl;
import at.technikumwien.dmsbackend.service.MinioService;
import at.technikumwien.dmsbackend.service.mapper.DocumentMapper;
import at.technikumwien.dmsbackend.config.RabbitMQConfig;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import ta.technikumwien.dmsocr.persistence.DocumentIndex;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

class DocumentServiceImplTest {

    @Mock private DocumentRepository documentRepository;
    @Mock private DocumentMapper documentMapper;
    @Mock private RabbitTemplate rabbitTemplate;
    @Mock private MinioService minioService;
    @Mock private DocumentIndexRepository documentIndexRepository;
    @Mock private ElasticsearchClient elasticsearchClient;

    private DocumentServiceImpl documentService;
    private DocumentDTO documentDTO;
    private DocumentEntity documentEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        documentService = new DocumentServiceImpl(
                documentRepository,
                documentMapper,
                rabbitTemplate,
                minioService,
                null,
                documentIndexRepository,
                elasticsearchClient
        );

        documentDTO = new DocumentDTO();
        documentDTO.setTitle("Test Document");
        documentDTO.setDescription("Test Description");
        documentDTO.setType("application/pdf");
        documentDTO.setSize(1024L);
        documentDTO.setUploadDate("2024-12-23");
        documentDTO.setFileData(new byte[]{1, 2, 3});

        documentEntity = new DocumentEntity();
        documentEntity.setId(1L);
        documentEntity.setTitle(documentDTO.getTitle());
        documentEntity.setDescription(documentDTO.getDescription());
        documentEntity.setType(documentDTO.getType());
        documentEntity.setSize(documentDTO.getSize());
        documentEntity.setUploadDate(LocalDate.parse(documentDTO.getUploadDate()));
    }

    @Test
    void testDeleteDocumentNotFound() {
        when(documentRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            documentService.deleteDocument(1L);
        });

        assertEquals("Document not found with ID: 1", exception.getMessage());
    }

    @Test
    void testUploadDocumentFailure() throws Exception {
        when(documentRepository.save(any(DocumentEntity.class))).thenReturn(documentEntity);
        doThrow(new RuntimeException("MinIO upload failed")).when(minioService).uploadFile(anyString(), any(), anyLong(), anyString());

        Exception exception = assertThrows(DocumentUploadException.class, () -> {
            documentService.uploadDocument(documentDTO);
        });

        assertEquals("Failed to upload document: MinIO upload failed", exception.getMessage());
    }

    @Test
    void uploadDocument_shouldThrowDocumentUploadException_onMinioFailure() throws Exception {
        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setTitle("Test Document");
        documentDTO.setDescription("Description");
        documentDTO.setType("pdf");
        documentDTO.setSize(1024L);
        documentDTO.setUploadDate(LocalDate.now().toString());
        documentDTO.setFileData(new byte[]{1, 2, 3});

        DocumentEntity savedEntity = new DocumentEntity();
        savedEntity.setId(1L);
        savedEntity.setTitle("Test Document");
        savedEntity.setDescription("Description");
        savedEntity.setFileKey("document-1");

        when(documentRepository.save(any(DocumentEntity.class))).thenReturn(savedEntity);
        when(documentMapper.mapToDto(any(DocumentEntity.class))).thenReturn(documentDTO);
        doThrow(new RuntimeException("Minio error")).when(minioService).uploadFile(anyString(), any(), anyLong(), anyString());

        DocumentUploadException thrown = assertThrows(DocumentUploadException.class, () -> {
            documentService.uploadDocument(documentDTO);
        });

        assertEquals("Failed to upload document: Minio error", thrown.getMessage());
    }

    @Test
    void getDocumentById_shouldReturnDocumentDTO() {
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setId(1L);
        documentEntity.setTitle("Test Document");
        documentEntity.setDescription("Description");

        DocumentDTO expectedDTO = new DocumentDTO();
        expectedDTO.setTitle("Test Document");
        expectedDTO.setDescription("Description");

        when(documentRepository.findById(1L)).thenReturn(Optional.of(documentEntity));
        when(documentMapper.mapToDto(documentEntity)).thenReturn(expectedDTO);

        DocumentDTO result = documentService.getDocumentById(1L);

        assertNotNull(result);
        assertEquals("Test Document", result.getTitle());
        assertEquals("Description", result.getDescription());
    }

    @Test
    void getDocumentById_shouldThrowEntityNotFoundException_ifNotFound() {
        when(documentRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            documentService.getDocumentById(1L);
        });

        assertEquals("Document not found with ID: 1", thrown.getMessage());
    }

    @Test
    void updateDocument_shouldUpdateDocument() throws Exception {
        Long documentId = 1L;
        DocumentDTO updateRequest = new DocumentDTO();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated Description");
        updateRequest.setType("pdf");
        updateRequest.setSize(2048L);
        updateRequest.setUploadDate(LocalDate.now().toString());
        updateRequest.setFileData(new byte[]{1, 2, 3});

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setId(documentId);
        documentEntity.setTitle("Old Title");
        documentEntity.setDescription("Old Description");

        when(documentRepository.findById(documentId)).thenReturn(Optional.of(documentEntity));
        when(documentMapper.mapToDto(any(DocumentEntity.class))).thenReturn(updateRequest);
        doNothing().when(minioService).uploadFile(anyString(), any(), anyLong(), anyString());

        DocumentDTO result = documentService.updateDocument(documentId, updateRequest);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
    }

    @Test
    void updateDocument_shouldThrowEntityNotFoundException_ifNotFound() {
        Long documentId = 1L;
        DocumentDTO updateRequest = new DocumentDTO();

        when(documentRepository.findById(documentId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            documentService.updateDocument(documentId, updateRequest);
        });

        assertEquals("Document not found with ID: 1", thrown.getMessage());
    }
}
