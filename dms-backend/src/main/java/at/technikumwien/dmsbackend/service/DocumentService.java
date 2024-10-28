package at.technikumwien.dmsbackend.service;


import at.technikumwien.dmsbackend.service.dto.DocumentDTO;

import javax.print.Doc;
import java.util.List;


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
}
