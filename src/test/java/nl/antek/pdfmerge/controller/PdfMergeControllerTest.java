package nl.antek.pdfmerge.controller;

import static java.nio.file.Files.readAllBytes;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import nl.antek.pdfmerge.service.PdfMergeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(PdfMergeController.class)
class PdfMergeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PdfMergeService pdfMergeService;

    @Test
    void shouldReturnMergedPdfFile() throws Exception {
        // Given
        var file1 = new MockMultipartFile("files", "file01.png", "image/png",
            readAllBytes(Path.of("src/test/resources/file01.png")));
        var file2 = new MockMultipartFile("files", "file02.jpg", "image/jpeg",
            readAllBytes(Path.of("src/test/resources/file02.jpg")));
        var file3 = new MockMultipartFile("files", "file03.png", "image/png",
            readAllBytes(Path.of("src/test/resources/file03.png")));
        var file4 = new MockMultipartFile("files", "file04.pdf", "application/pdf",
            readAllBytes(Path.of("src/test/resources/file04.pdf")));

        var mergedFile = new File("merged.pdf");
        Files.write(mergedFile.toPath(), "Merged PDF Content".getBytes());

        when(pdfMergeService.processFiles(new MultipartFile[]{file1, file2, file3, file4}))
            .thenReturn(mergedFile);

        // When & Then
        mockMvc.perform(multipart("/api/pdf/merge")
                .file(file1).file(file2).file(file3).file(file4))
            .andExpect(status().isOk())
            .andExpect(header().string(CONTENT_DISPOSITION, "attachment; filename=result.pdf"))
            .andExpect(content().contentType(MediaType.APPLICATION_PDF))
            .andReturn();

        // Clean up
        mergedFile.delete();
    }

    @Test
    void shouldReturnBadRequestWhenNoFilesProvided() throws Exception {
        // Given & When & Then
        mockMvc.perform(multipart("/api/pdf/merge"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnServerErrorWhenIOExceptionOccurs() throws Exception {
        // Given
        var file1 = new MockMultipartFile("files", "file01.png", "image/png",
            readAllBytes(Path.of("src/test/resources/file01.png")));

        when(pdfMergeService.processFiles(new MultipartFile[]{file1}))
            .thenThrow(new IOException("Failed to process files"));

        // When & Then
        mockMvc.perform(multipart("/api/pdf/merge")
                .file(file1))
            .andExpect(status().is5xxServerError());
    }
}
