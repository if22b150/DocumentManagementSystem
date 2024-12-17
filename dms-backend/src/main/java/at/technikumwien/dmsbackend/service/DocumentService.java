package at.technikumwien.dmsbackend.service;


import java.util.List;

import at.technikumwien.dmsbackend.service.dto.DocumentDTO;


public interface DocumentService {
    DocumentDTO uploadDocument(DocumentDTO documentDTO);

    // Retrieve a document by its ID
    DocumentDTO getDocumentById(Long id);

    // Retrieve a list of all documents
    List<DocumentDTO> getAllDocuments();

    DocumentDTO updateDocument(Long id, DocumentDTO updateRequest);

    // Delete a document by its ID
    void deleteDocument(Long id);

    List<DocumentDTO> searchDocuments(String query);

    // Retrieve metadata for a specific document by its ID
    DocumentDTO getDocumentMetadata(Long id);

    List<DocumentDTO> searchDocumentsInContent(String searchTerm);
}
