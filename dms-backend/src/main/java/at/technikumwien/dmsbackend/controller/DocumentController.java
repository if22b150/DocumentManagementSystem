package at.technikumwien.dmsbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import at.technikumwien.dmsbackend.dto.DocumentMetadataResponseDTO;
import at.technikumwien.dmsbackend.dto.DocumentResponseDTO;
import at.technikumwien.dmsbackend.dto.DocumentUpdateRequestDTO;
import at.technikumwien.dmsbackend.dto.SearchDocumentResponseDTO;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    /**
     * POST /documents/upload : Upload a document
     * Uploads a document for processing.
     *
     * Possible Responses:
     * - 200: Document uploaded successfully
     * - 400: Bad request (e.g., invalid file format or missing file)
     * 
     * @param file The document file to upload (required)
     * @return Document uploaded successfully (status code 200)
     */
    @PostMapping("/upload")
    public ResponseEntity<DocumentResponseDTO> uploadDocument(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        DocumentResponseDTO response = DocumentResponseDTO.builder()
                .id(1L)
                .url("http://localhost:8080/documents/1")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /documents/{id} : Get document by ID
     * Retrieve the document using its ID.
     *
     * Possible Responses:
     * - 200: Document retrieved successfully
     * - 404: Document not found
     *
     * @param id The ID of the document (required)
     * @return Document retrieved successfully (status code 200)
     *         or Document not found (status code 404)
     */
    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponseDTO> getDocumentById(@PathVariable String id) {
        if(!"1".equals(id)) {
            return ResponseEntity.status(404).build();
        }
        DocumentResponseDTO response = DocumentResponseDTO.builder()
                .id(Long.parseLong(id))
                .url("http://localhost:8080/documents/" + id)
                .title("Document " + id)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<List<DocumentResponseDTO>> getDocuments() {
        DocumentResponseDTO doc1 = DocumentResponseDTO.builder()
                .id(Long.valueOf(1))
                .url("http://localhost:8080/documents/" + 1)
                .title("Document 1")
                .build();
        DocumentResponseDTO doc2 = DocumentResponseDTO.builder()
                .id(Long.valueOf(2))
                .url("http://localhost:8080/documents/" + 2)
                .title("Document 2")
                .build();
        DocumentResponseDTO doc3 = DocumentResponseDTO.builder()
                .id(Long.valueOf(3))
                .url("http://localhost:8080/documents/" + 3)
                .title("Document 3")
                .build();
        return ResponseEntity.ok(Arrays.asList(doc1, doc2, doc3));
    }

    /**
     * PUT /documents/{id} : Update a document
     * Updates the metadata or content of a document.
     *
     * Possible Responses:
     * - 200: Document updated successfully
     * - 404: Document not found
     * - 400: Bad request (e.g., invalid update body)
     *
     * @param id The ID of the document to update (required)
     * @param updateRequest The updated document metadata or content (required)
     * @return Document updated successfully (status code 200)
     *         or Document not found (status code 404)
     *         or Bad request (status code 400)
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateDocument(@PathVariable String id, @RequestBody DocumentUpdateRequestDTO updateRequest) {
        if (!"1".equals(id)) {
            return ResponseEntity.status(404).build();
        }
        if (updateRequest.getMetadata() == null || updateRequest.getContent() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("Document updated successfully");
    }

    /**
     * DELETE /documents/{id} : Delete a document
     * Deletes the document by ID.
     *
     * Possible Responses:
     * - 200: Document deleted successfully
     * - 404: Document not found
     *
     * @param id The ID of the document to delete (required)
     * @return Document deleted successfully (status code 200)
     *         or Document not found (status code 404)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable String id) {
        if (!"1".equals(id)) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok("Document deleted successfully");
    }

    /**
     * GET /documents/search : Search documents
     * Search for documents by metadata or content.
     *
     * Possible Responses:
     * - 200: Search results returned successfully
     * - 400: Bad request (e.g., invalid query parameter)
     *
     * @param query The search query (required)
     * @return Search results (status code 200)
     *         or Bad request (status code 400)
     */
    @GetMapping("/search")
    public ResponseEntity<List<SearchDocumentResponseDTO>> searchDocuments(@RequestParam("query") String query) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        SearchDocumentResponseDTO doc1 = SearchDocumentResponseDTO.builder()
                .id(1L)
                .title("Document Title")
                .summary("Summary of document content")
                .build();

        SearchDocumentResponseDTO doc2 = SearchDocumentResponseDTO.builder()
                .id(2L)
                .title("Another Document")
                .summary("Another summary")
                .build();

        return ResponseEntity.ok(Arrays.asList(doc1, doc2));
    }

    /**
     * GET /documents/{id}/metadata : Get document metadata
     * Retrieves metadata for a specific document by ID.
     *
     * Possible Responses:
     * - 200: Metadata retrieved successfully
     * - 404: Document not found
     *
     * @param id The ID of the document (required)
     * @return Metadata retrieved successfully (status code 200)
     *         or Document not found (status code 404)
     */
    @GetMapping("/{id}/metadata")
    public ResponseEntity<DocumentMetadataResponseDTO> getDocumentMetadata(@PathVariable String id) {
        if (!"1".equals(id)) {
            return ResponseEntity.status(404).build();
        }
        DocumentMetadataResponseDTO metadataResponse = DocumentMetadataResponseDTO.builder()
                .title("Document Title")
                .description("Document description")
                .createdAt("2023-09-22T10:00:00Z")
                .updatedAt("2023-09-22T11:00:00Z")
                .build();

        return ResponseEntity.ok(metadataResponse);
    }
}
