package at.technikumwien.dmsbackend.controller;

import at.technikumwien.dmsbackend.persistence.entity.DocumentEntity;
import at.technikumwien.dmsbackend.service.DocumentService;
import at.technikumwien.dmsbackend.service.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {
    @Autowired
    private DocumentService documentService;

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
    public ResponseEntity<List<DocumentDTO>> getDocuments() {
        List<DocumentDTO> documents = documentService.getAllDocuments();
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
}
