package at.technikumwien.dmsbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentMetadataResponseDTO {
    private String title;
    private String description;
    private String createdAt;
    private String updatedAt;
}
