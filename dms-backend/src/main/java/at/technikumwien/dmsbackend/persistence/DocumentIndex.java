package at.technikumwien.dmsbackend.persistence;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "documents")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentIndex {
    @Id
    Long documentId;

    String content;
}
