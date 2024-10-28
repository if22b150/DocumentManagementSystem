package at.technikumwien.dmsbackend.service.impl;

import at.technikumwien.dmsbackend.persistence.entity.DocumentEntity;
import at.technikumwien.dmsbackend.persistence.repository.DocumentRepository;
import at.technikumwien.dmsbackend.service.DocumentService;
import at.technikumwien.dmsbackend.service.dto.DocumentDTO;
import at.technikumwien.dmsbackend.service.mapper.DocumentMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Transactional
@RequiredArgsConstructor
public class DocumentImpl implements DocumentService {
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private DocumentMapper documentMapper;

    @Override
    public DocumentDTO uploadDocument(DocumentDTO documentDTO) {
        DocumentEntity entity = DocumentEntity.builder()
                .title(documentDTO.getTitle())
                .description(documentDTO.getDescription())
                .type(documentDTO.getType())
                .size(documentDTO.getSize())
                .uploadDate(LocalDate.parse(documentDTO.getUploadDate()))
                .fileData(documentDTO.getFileData())
                .build();

        documentRepository.save(entity);
        return documentMapper.mapToDto(entity);
    }

    @Override
    public DocumentDTO getDocumentById(Long id) {
        Optional<DocumentEntity> documentEntityOptional = documentRepository.findById(id);

        if (documentEntityOptional.isPresent())
            return documentMapper.mapToDto(documentEntityOptional.get());
        else
            throw new EntityNotFoundException("Document not found with ID: " + id);
    }

    @Override
    public List<DocumentDTO> getAllDocuments() {
        return documentRepository.findAll().stream()
                .map(documentMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public DocumentDTO updateDocument(Long id, DocumentDTO updateRequest) {
        DocumentEntity existingDocument = documentMapper.mapToEntity(getDocumentById(id));

        existingDocument.setTitle(updateRequest.getTitle());
        existingDocument.setDescription(updateRequest.getDescription());
        existingDocument.setType(updateRequest.getType());
        existingDocument.setSize(updateRequest.getSize());
        existingDocument.setUploadDate(LocalDate.parse(updateRequest.getUploadDate()));
        existingDocument.setFileData(updateRequest.getFileData());

        documentRepository.save(existingDocument);
        return documentMapper.mapToDto(existingDocument);
    }

    @Override
    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
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
