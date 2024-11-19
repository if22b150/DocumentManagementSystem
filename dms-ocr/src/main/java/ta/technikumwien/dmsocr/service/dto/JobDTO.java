package ta.technikumwien.dmsocr.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobDTO  implements Serializable {
    private Long documentId;
    private String fileKey;
}
