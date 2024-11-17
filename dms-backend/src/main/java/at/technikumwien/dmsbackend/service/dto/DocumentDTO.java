package at.technikumwien.dmsbackend.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDTO {
    private Long id;
    @NotBlank(message = "Title is required.")
    private String title;
    @NotBlank(message = "Description is required.")
    private String description;
    @NotBlank(message = "Type is required.")
    private String type;
    @NotNull(message = "Size is required.")
    private Long size;
    @NotBlank(message = "Upload date is required.")
    private String uploadDate;
    private byte[] fileData; // Only set this when file data is fetched from MinIO
    private String fileKey;
}