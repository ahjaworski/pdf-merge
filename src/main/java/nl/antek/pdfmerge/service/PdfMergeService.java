package nl.antek.pdfmerge.service;

import static org.apache.pdfbox.Loader.loadPDF;
import static org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject.createFromFileByContent;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class PdfMergeService {

    public File processFiles(MultipartFile[] files) throws IOException {
        var outputFile = File.createTempFile("processed-", ".pdf");

        log.info("Merging PDF from {} files", files.length);
        try (var document = new PDDocument()) {
            for (var file : files) {
                var contentType = file.getContentType();

                if (contentType != null && contentType.startsWith(APPLICATION_PDF_VALUE)) {
                    var tempPdfFile = convertMultipartFileToFile(file);
                    var inputPdf = loadPDF(tempPdfFile);
                    var pdfRenderer = new PDFRenderer(inputPdf);

                    int numberOfPages = inputPdf.getNumberOfPages();
                    log.debug("Total pages of pdf {} to be converting: {}", file.getName(), numberOfPages);

                    var dpi = 600;
                    for (int i = 0; i < numberOfPages; ++i) {
                        var image = pdfRenderer.renderImageWithDPI(i, dpi, ImageType.RGB);
                        var compressedImage = compressImage(image);

                        var page = new PDPage();
                        document.addPage(page);
                        try (var contentStream = new PDPageContentStream(document, page)) {
                            var pdImage =
                                createFromFileByContent(convertBufferedImageToFile(compressedImage), document);

                            var x_pos = page.getCropBox().getWidth();
                            var y_pos = page.getCropBox().getHeight();
                            var x_adjusted = ( x_pos - pdImage.getWidth() ) / 2 + page.getCropBox().getLowerLeftX();
                            var y_adjusted = ( y_pos - pdImage.getHeight() ) / 2 + page.getCropBox().getLowerLeftY();
                            contentStream.drawImage(pdImage, x_adjusted, y_adjusted, pdImage.getWidth(), pdImage.getHeight());
                        }
                    }

                    inputPdf.close();
                } else if (contentType != null && contentType.startsWith("image")) {
                    var image = ImageIO.read(file.getInputStream());
                    var compressedImage = compressImage(image);

                    var page = new PDPage();
                    document.addPage(page);
                    try (var contentStream = new PDPageContentStream(document, page)) {
                        var pdImage = createFromFileByContent(
                            convertBufferedImageToFile(compressedImage), document);
                        var x_pos = page.getCropBox().getWidth();
                        var y_pos = page.getCropBox().getHeight();
                        var x_adjusted = ( x_pos - pdImage.getWidth() ) / 2 + page.getCropBox().getLowerLeftX();
                        var y_adjusted = ( y_pos - pdImage.getHeight() ) / 2 + page.getCropBox().getLowerLeftY();
                        contentStream.drawImage(pdImage, x_adjusted, y_adjusted, pdImage.getWidth(), pdImage.getHeight());  // Pas posities aan zoals nodig
                    }
                }
            }

            document.save(outputFile);
        }

        return outputFile;
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        var tempFile = File.createTempFile("temp-", ".tmp");
        file.transferTo(tempFile);
        return tempFile;
    }

    private BufferedImage compressImage(BufferedImage originalImage) throws IOException {
        var outputStream = new ByteArrayOutputStream();
        Thumbnails.of(originalImage)
            .size(600, 800)
            .keepAspectRatio(true)
            .outputFormat("png")
            .outputQuality(1)
            .toOutputStream(outputStream);

        return ImageIO.read(new ByteArrayInputStream(outputStream.toByteArray()));
    }

    private File convertBufferedImageToFile(BufferedImage image) throws IOException {
        var tempFile = File.createTempFile("temp-image-", ".png");
        ImageIO.write(image, "png", tempFile);
        return tempFile;
    }

}
