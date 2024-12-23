package at.technikumwien.dmsbackend.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import at.technikumwien.dmsbackend.config.RabbitMQConfig;
import at.technikumwien.dmsbackend.persistence.entity.DocumentEntity;
import at.technikumwien.dmsbackend.persistence.repository.DocumentRepository;
import at.technikumwien.dmsbackend.service.dto.DocumentDTO;
import at.technikumwien.dmsbackend.service.mapper.DocumentMapper;

class DocumentServiceImplTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentMapper documentMapper;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private DocumentServiceImpl documentService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadDocument() {
        DocumentDTO documentDTO = DocumentDTO.builder()
                .title("Title")
                .description("Description")
                .type("Type")
                .size(123L)
                .uploadDate("2024-11-04")
                .build();
    
        DocumentEntity documentEntity = DocumentEntity.builder()
                .id(1L)  
                .title("Title")
                .description("Description")
                .type("Type")
                .size(123L)
                .uploadDate(LocalDate.parse("2024-11-04"))
                .build();
    
        when(documentRepository.save(any(DocumentEntity.class))).thenAnswer(invocation -> {
            DocumentEntity savedEntity = invocation.getArgument(0);
            savedEntity.setId(1L);
            return savedEntity;
        });
    
        when(documentMapper.mapToDto(any(DocumentEntity.class))).thenReturn(documentDTO);
        
        DocumentDTO result = documentService.uploadDocument(documentDTO);
        
        assertNotNull(result);
        verify(rabbitTemplate, times(1)).convertAndSend(RabbitMQConfig.OCR_QUEUE, "Document uploaded with ID: " + documentEntity.getId());
    }
    
    

    @Test
    void testGetDocumentById() {
        DocumentEntity documentEntity = DocumentEntity.builder()
                .id(1L)
                .title("Title")
                .description("Description")
                .type("Type")
                .size(123L)
                .uploadDate(LocalDate.now())
                .build();

        when(documentRepository.findById(1L)).thenReturn(Optional.of(documentEntity));
        when(documentMapper.mapToDto(documentEntity)).thenReturn(DocumentDTO.builder()
                .id(1L)
                .title("Title")
                .description("Description")
                .type("Type")
                .size(123L)
                .uploadDate("2024-11-04")
                .build());

        DocumentDTO result = documentService.getDocumentById(1L);

        assertEquals("Title", result.getTitle());
    }

    @Test
    void testGetAllDocuments() {
        DocumentEntity documentEntity = DocumentEntity.builder()
                .id(1L)
                .title("Title")
                .description("Description")
                .type("Type")
                .size(123L)
                .uploadDate(LocalDate.now())
                .build();

        when(documentRepository.findAll()).thenReturn(Collections.singletonList(documentEntity));
        when(documentMapper.mapToDto(documentEntity)).thenReturn(DocumentDTO.builder()
                .id(1L)
                .title("Title")
                .description("Description")
                .type("Type")
                .size(123L)
                .uploadDate("2024-11-04")
                .build());

        List<DocumentDTO> results = documentService.getAllDocuments();

        assertEquals(1, results.size());
    }

    @Test
    void testGetDocumentMetadata() {
        DocumentEntity documentEntity = DocumentEntity.builder()
                .id(1L)
                .title("Title")
                .description("Description")
                .type("Type")
                .size(123L)
                .uploadDate(LocalDate.now())
                .build();

        when(documentRepository.findById(1L)).thenReturn(Optional.of(documentEntity));
        when(documentMapper.mapToDto(documentEntity)).thenReturn(DocumentDTO.builder()
                .id(1L)
                .title("Title")
                .description("Description")
                .type("Type")
                .size(123L)
                .uploadDate("2024-11-04")
                .build());

        DocumentDTO result = documentService.getDocumentMetadata(1L);

        assertEquals("Title", result.getTitle());
    }
}
