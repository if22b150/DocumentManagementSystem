package at.technikumwien.dmsbackend.service.mapper;

import at.technikumwien.dmsbackend.persistence.entity.DocumentEntity;
import at.technikumwien.dmsbackend.service.dto.DocumentDTO;
import org.springframework.stereotype.Component;
import java.time.LocalDate;


@Component
public class DocumentMapper extends AbstractMapper<DocumentEntity, DocumentDTO> {

    @Override
    public DocumentDTO mapToDto(DocumentEntity source) {
        return DocumentDTO.builder()
                .id(source.getId())
                .title(source.getTitle())
                .description(source.getDescription())
                .type(source.getType())
                .size(source.getSize())
                .uploadDate(source.getUploadDate().toString())
                .build();
    }

    public DocumentEntity mapToEntity(DocumentDTO source) {
        return DocumentEntity.builder()
                .id(source.getId())
                .title(source.getTitle())
                .description(source.getDescription())
                .type(source.getType())
                .size(source.getSize())
                .uploadDate(LocalDate.parse(source.getUploadDate()))
                .fileKey("document-" + source.getId()) // Generate the MinIO file key
                .build();
    }
}