package at.technikumwien.dmsbackend.controller;

import at.technikumwien.dmsbackend.service.DocumentService;
import at.technikumwien.dmsbackend.service.MinioService;
import at.technikumwien.dmsbackend.service.dto.DocumentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DocumentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DocumentService documentService;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private DocumentController documentController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(documentController).build();
    }

    @Test
    void testDownloadDocument() throws Exception {
        DocumentDTO documentDTO = DocumentDTO.builder()
                .id(1L)
                .fileKey("test-file-key")
                .title("Test File")
                .build();

        when(documentService.getDocumentById(1L)).thenReturn(documentDTO);
        when(minioService.getFile("test-file-key"))
                .thenReturn(new ByteArrayInputStream("Test File Content".getBytes()));

        mockMvc.perform(get("/api/v1/documents/1/download"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"Test_File.pdf\""))
                .andExpect(content().string("Test File Content"));
    }

    @Test
    void testSearchDocumentsWithContentFilter() throws Exception {
        DocumentDTO documentDTO = DocumentDTO.builder()
                .id(1L)
                .title("Filtered Document")
                .description("Description")
                .type("Type")
                .size(123L)
                .uploadDate("2024-11-04")
                .build();

        when(documentService.searchDocumentsInContent("contentFilter"))
                .thenReturn(Collections.singletonList(documentDTO));

        mockMvc.perform(get("/api/v1/documents")
                        .param("content", "contentFilter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Filtered Document"));
    }

    @Test
    void testCreateDocument() throws Exception {
        DocumentDTO documentDTO = DocumentDTO.builder()
                .id(1L)
                .title("Title")
                .description("Description")
                .type("Type")
                .size(123L)
                .uploadDate("2024-11-04")
                .build();

        when(documentService.uploadDocument(any(DocumentDTO.class))).thenReturn(documentDTO);

        mockMvc.perform(post("/api/v1/documents")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Title\", \"description\":\"Description\", \"type\":\"Type\", \"size\":123, \"uploadDate\":\"2024-11-04\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    void testGetDocumentById() throws Exception {
        DocumentDTO documentDTO = DocumentDTO.builder()
                .id(1L)
                .title("Title")
                .description("Description")
                .type("Type")
                .size(123L)
                .uploadDate("2024-11-04")
                .build();

        when(documentService.getDocumentById(1L)).thenReturn(documentDTO);

        mockMvc.perform(get("/api/v1/documents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    void testGetDocuments() throws Exception {
        DocumentDTO documentDTO = DocumentDTO.builder()
                .id(1L)
                .title("Title")
                .description("Description")
                .type("Type")
                .size(123L)
                .uploadDate("2024-11-04")
                .build();

        when(documentService.getAllDocuments()).thenReturn(Collections.singletonList(documentDTO));

        mockMvc.perform(get("/api/v1/documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testUpdateDocument() throws Exception {
        DocumentDTO documentDTO = DocumentDTO.builder()
                .id(1L)
                .title("Updated Title")
                .description("Description")
                .type("Type")
                .size(123L)
                .uploadDate("2024-11-04")
                .build();

        when(documentService.updateDocument(anyLong(), any(DocumentDTO.class))).thenReturn(documentDTO);

        mockMvc.perform(put("/api/v1/documents/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Updated Title\", \"description\":\"Description\", \"type\":\"Type\", \"size\":123, \"uploadDate\":\"2024-11-04\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void testDeleteDocument() throws Exception {
        mockMvc.perform(delete("/api/v1/documents/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testSearchDocuments() throws Exception {
        DocumentDTO documentDTO = DocumentDTO.builder()
                .id(1L)
                .title("Title")
                .description("Description")
                .type("Type")
                .size(123L)
                .uploadDate("2024-11-04")
                .build();

        when(documentService.searchDocuments("Title")).thenReturn(Collections.singletonList(documentDTO));

        mockMvc.perform(get("/api/v1/documents/search")
                .param("query", "Title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Title"));
    }

    @Test
    void testGetDocumentMetadata() throws Exception {
        DocumentDTO documentDTO = DocumentDTO.builder()
                .id(1L)
                .title("Title")
                .description("Description")
                .type("Type")
                .size(123L)
                .uploadDate("2024-11-04")
                .build();

        when(documentService.getDocumentMetadata(1L)).thenReturn(documentDTO);

        mockMvc.perform(get("/api/v1/documents/1/metadata"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Title"));
    }
}
