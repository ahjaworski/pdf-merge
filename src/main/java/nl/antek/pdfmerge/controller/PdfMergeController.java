package nl.antek.pdfmerge.controller;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.APPLICATION_PDF;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import lombok.RequiredArgsConstructor;
import nl.antek.pdfmerge.service.PdfMergeService;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/pdf")
public class PdfMergeController {

    private final PdfMergeService pdfMergeService;

    @PostMapping("/merge")
    public ResponseEntity<byte[]> processFiles(@RequestParam("files") MultipartFile[] files) throws IOException {
        var processedFile = pdfMergeService.processFiles(files);
        var pdfContent = Files.readAllBytes(processedFile.toPath());
        processedFile.delete();

        return ResponseEntity.ok()
            .header(CONTENT_DISPOSITION, "attachment; filename=result.pdf")
            .contentType(APPLICATION_PDF)
            .body(pdfContent);
    }
}
