package at.technikumwien.dmsbackend.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import at.technikumwien.dmsbackend.service.MinioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${minio.bucket-name}")
    private String bucketName;

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

            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, "Document uploaded with ID: " + entity.getId());
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

        try {
            minioService.deleteFile(documentEntity.getFileKey()); // Call deleteFile from MinioService
            logger.info("File deleted from MinIO with key: {}", documentEntity.getFileKey());
        } catch (Exception e) {
            logger.warn("Failed to delete file from MinIO: {}", e.getMessage(), e);
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
    public DocumentDTO getDocumentMetadata(Long id) {
        return getDocumentById(id);
    }
}
