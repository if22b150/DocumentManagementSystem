package at.technikumwien.dmsbackend.service.impl;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import at.technikumwien.dmsbackend.persistence.DocumentIndex;
import at.technikumwien.dmsbackend.persistence.repository.DocumentIndexRepository;
import at.technikumwien.dmsbackend.service.MinioService;
import at.technikumwien.dmsbackend.service.dto.OCRJobDTO;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.springframework.stereotype.Component;

import at.technikumwien.dmsbackend.config.RabbitMQConfig;
import at.technikumwien.dmsbackend.exception.DocumentUploadException;
import at.technikumwien.dmsbackend.persistence.entity.DocumentEntity;
import at.technikumwien.dmsbackend.persistence.repository.DocumentRepository;
import at.technikumwien.dmsbackend.service.DocumentService;
import at.technikumwien.dmsbackend.service.dto.DocumentDTO;
import at.technikumwien.dmsbackend.service.mapper.DocumentMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@Transactional
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final RabbitTemplate rabbitTemplate;
    private final MinioService minioService;
    private final ObjectMapper objectMapper;
    private final DocumentIndexRepository documentIndexRepository;
    private final ElasticsearchClient elasticsearchClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    MessagePostProcessor messagePostProcessor = message -> {
        message.getMessageProperties().setHeader("__TypeId__", "JobDTO");
        return message;
    };

    @Override
    public DocumentDTO uploadDocument(DocumentDTO documentDTO) {
        DocumentEntity entity = DocumentEntity.builder()
                .title(documentDTO.getTitle())
                .description(documentDTO.getDescription())
                .type(documentDTO.getType())
                .size(documentDTO.getSize())
                .uploadDate(LocalDate.parse(documentDTO.getUploadDate()))
//                .fileData(documentDTO.getFileData())
                .build();

        // Save the entity to generate the ID
        entity = documentRepository.save(entity);

        // Step 2: Set the fileKey and save the entity again
        String fileKey = "document-" + entity.getId();
        entity.setFileKey(fileKey);
        entity = documentRepository.save(entity);

        try {
            // Upload the file data to MinIO
            minioService.uploadFile(fileKey, new ByteArrayInputStream(documentDTO.getFileData()), documentDTO.getFileData().length, documentDTO.getType());
            OCRJobDTO ocrJobDTO = new OCRJobDTO(entity.getId(), fileKey);
            rabbitTemplate.convertAndSend(RabbitMQConfig.OCR_QUEUE, ocrJobDTO, messagePostProcessor);
            logger.info("Message sent to RabbitMQ: Document uploaded with ID: {}", entity.getId());
        } catch (Exception e) {
            throw new DocumentUploadException("Failed to upload document: " + e.getMessage());
        }
        return documentMapper.mapToDto(entity);
    }

    @Override
    public DocumentDTO getDocumentById(Long id) {
        DocumentEntity documentEntity = documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document not found with ID: " + id));

        return documentMapper.mapToDto(documentEntity);
    }

    @Override
    public List<DocumentDTO> getAllDocuments() {
        return documentRepository.findAll().stream()
                .map(documentMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public DocumentDTO updateDocument(Long id, DocumentDTO updateRequest) {
        DocumentEntity existingDocument = documentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Document not found with ID: " + id));

        existingDocument.setTitle(updateRequest.getTitle());
        existingDocument.setDescription(updateRequest.getDescription());
        existingDocument.setType(updateRequest.getType());
        existingDocument.setSize(updateRequest.getSize());
        existingDocument.setUploadDate(LocalDate.parse(updateRequest.getUploadDate()));

        documentRepository.save(existingDocument);

        if (updateRequest.getFileData() != null) {
            String fileKey = existingDocument.getFileKey();
            try {
                // Update the file in MinIO
                minioService.uploadFile(fileKey, new ByteArrayInputStream(updateRequest.getFileData()), updateRequest.getSize(), updateRequest.getType());
            } catch (Exception e) {
                throw new DocumentUploadException("Failed to update document file: " + e.getMessage());
            }
        }

        return documentMapper.mapToDto(existingDocument);
    }

    @Override
    public void deleteDocument(Long id) {
        DocumentEntity documentEntity = documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document not found with ID: " + id));

        documentRepository.deleteById(id);

        // delete file from minio
        try {
            minioService.deleteFile(documentEntity.getFileKey()); // Call deleteFile from MinioService
            logger.info("File deleted from MinIO with key: {}", documentEntity.getFileKey());
        } catch (Exception e) {
            logger.warn("Failed to delete file from MinIO: {}", e.getMessage(), e);
        }

        // delete index from elasticsearch
        try {
            elasticsearchClient.delete(d -> d
                    .index("documents") // Elasticsearch index name
                    .id(String.valueOf(documentEntity.getId())) // Document ID in Elasticsearch
            );
            logger.info("Document deleted from Elasticsearch with ID: {}", documentEntity.getId());
        } catch (Exception e) {
            logger.warn("Failed to delete document from Elasticsearch: {}", e.getMessage(), e);
        }
    }

    @Override
    public List<DocumentDTO> searchDocuments(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<DocumentEntity> documentEntities = documentRepository.searchByQuery(query);
        return documentMapper.mapToDto(documentEntities);
    }

    @Override
    public List<DocumentDTO> searchDocumentsInContent(String searchTerm) {
        try {
            // Build the search query
            Query query = Query.of(q -> q
                    .match(m -> m
                            .field("content") // Field to search
                            .query(searchTerm) // Search term
                    )
            );

            // Build the search request
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("documents") // Index name
                    .query(query) // Search query
            );

            // Execute the search
            SearchResponse<DocumentIndex> searchResponse = elasticsearchClient.search(searchRequest, DocumentIndex.class);

            // Map results to DTOs
            List<DocumentDTO> result = searchResponse.hits().hits().stream()
                    .map(Hit::source)
                    .map(documentIndex -> {
                        DocumentEntity document = documentRepository.findById(documentIndex.getDocumentId()).get();
                        return documentMapper.mapToDto(document);
                    })
                    .toList();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to search Elasticsearch", e);
        }
    }

    @Override
    public DocumentDTO getDocumentMetadata(Long id) {
        return getDocumentById(id);
    }
}
