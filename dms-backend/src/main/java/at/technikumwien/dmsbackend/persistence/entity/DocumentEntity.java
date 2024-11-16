package at.technikumwien.dmsbackend.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "document")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Long size;

    @Column(name = "upload_date", nullable = false)
    private LocalDate uploadDate;

    @Column(name = "file_key", nullable = false, unique = true)
    private String fileKey; // Reference to the file in MinIO
}
