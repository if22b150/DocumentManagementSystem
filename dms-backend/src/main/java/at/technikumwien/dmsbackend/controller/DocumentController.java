package at.technikumwien.dmsbackend.controller;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import at.technikumwien.dmsbackend.service.MinioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import at.technikumwien.dmsbackend.service.DocumentService;
import at.technikumwien.dmsbackend.service.dto.DocumentDTO;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/documents")
public class DocumentController {
    
    private final DocumentService documentService;
    private final MinioService minioService;

    @PostMapping
    public ResponseEntity<DocumentDTO> create(@Valid @RequestBody DocumentDTO document) {
        DocumentDTO createdDocument = documentService.uploadDocument(document);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDocument);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDTO> getDocumentById(@Valid @PathVariable Long id) {
        DocumentDTO document = documentService.getDocumentById(id);
        return ResponseEntity.ok(document);
    }

    @GetMapping()
    public ResponseEntity<List<DocumentDTO>> getDocuments(@RequestParam(value = "content", required = false) String searchTerm) {
        List<DocumentDTO> documents = searchTerm != null && !searchTerm.isBlank()
                ? documentService.searchDocumentsInContent(searchTerm)
                : documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentDTO> updateDocument(@Valid @PathVariable Long id, @Valid @RequestBody DocumentDTO updatedDTO) {
        DocumentDTO updatedDocument = documentService.updateDocument(id, updatedDTO);
        return ResponseEntity.ok(updatedDocument);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@Valid @PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @GetMapping("/search")
    public ResponseEntity<List<DocumentDTO>> searchDocuments(@RequestParam("query") String query) {
        List<DocumentDTO> foundDocuments = documentService.searchDocuments(query);
        return ResponseEntity.ok(foundDocuments);
    }

    @GetMapping("/{id}/metadata")
    public ResponseEntity<DocumentDTO> getDocumentMetadata(@Valid @PathVariable Long id) {
        DocumentDTO documentMetadata = documentService.getDocumentMetadata(id);
        return ResponseEntity.ok(documentMetadata);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        // Fetch document metadata from the database
        DocumentDTO document = documentService.getDocumentById(id);

        try {
            // Fetch file data from MinIO using the fileKey
            InputStream fileInputStream = minioService.getFile(document.getFileKey());
            byte[] fileData = fileInputStream.readAllBytes();

            String filename = (document.getTitle() != null && !document.getTitle().isEmpty())
                    ? document.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_")
                    : document.getFileKey();

            // Return the file as a downloadable response
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + filename + ".pdf\"")
                    .contentType(org.springframework.http.MediaType.parseMediaType("application/octet-stream"))
                    .body(fileData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch file from MinIO: " + e.getMessage(), e);
        }
    }
}
