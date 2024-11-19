package at.technikumwien.dmsbackend.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OCRJobDTO implements Serializable {
    private Long documentId;
    private String fileKey;
}
