package at.technikumwien.dmsbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchDocumentResponseDTO {
    private Long id;
    private String title;
    private String summary;
}
