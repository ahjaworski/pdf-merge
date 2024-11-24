package nl.antek.pdfmerge.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class PdfMergeServiceTest {

    @InjectMocks
    private PdfMergeService pdfMergeService;

    @Test
    void processFiles() throws IOException {
        // Given
        var file1 = new MockMultipartFile(
            "file02",
            "file02.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            new ClassPathResource("file02.jpg").getContentAsByteArray()
        );
        var file2 = new MockMultipartFile(
            "file04",
            "file04.pdf",
            MediaType.APPLICATION_PDF.toString(),
            new ClassPathResource("file04.pdf").getContentAsByteArray()
        );

        // When
        var files = new MultipartFile[]{file1, file2};
        var result = pdfMergeService.processFiles(files);

        // Then
        assertThat(result).satisfies(r -> {
            assertThat(r).exists();
            assertThat(r.length()).isGreaterThan(1000);
        });
    }
}
