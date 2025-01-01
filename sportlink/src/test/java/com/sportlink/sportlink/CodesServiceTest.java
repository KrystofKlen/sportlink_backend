package com.sportlink.sportlink;

import com.sportlink.sportlink.codes.CodesService;
import org.hibernate.annotations.processing.Exclude;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CodesServiceTest {

    private CodesService codesService;

    @BeforeEach
    public void setUp() {
        codesService = new CodesService();
    }

    @Test
    @Disabled
    public void testGenerateBarCode() throws Exception {
        // Given
        String content = "123456789";

        // When
        BufferedImage barcodeImage = codesService.generateBarCode(content);

        // Then
        assertThat(barcodeImage).isNotNull();
        assertThat(barcodeImage.getWidth()).isEqualTo(300);
        assertThat(barcodeImage.getHeight()).isEqualTo(100);

        // Save to file for visual verification
        ImageIO.write(barcodeImage, "png", new File("test_barcode.png"));
    }

    @Test
    @Disabled
    public void testGenerateQRCode() throws Exception {
        // Given
        String content = "https://example.com";

        // When
        BufferedImage qrCodeImage = CodesService.generateQRCode(content);

        // Then
        assertThat(qrCodeImage).isNotNull();
        assertThat(qrCodeImage.getWidth()).isEqualTo(300);
        assertThat(qrCodeImage.getHeight()).isEqualTo(300);

        // Save to file for visual verification
        ImageIO.write(qrCodeImage, "png", new File("test_qrcode.png"));
    }

    @Test
    public void testReadCodeFromBarcode() throws Exception {
        // Given
        String expectedContent = "123456789";
        BufferedImage barcodeImage = codesService.generateBarCode(expectedContent);

        // When
        String decodedContent = CodesService.readCode(barcodeImage);

        // Then
        assertThat(decodedContent).isEqualTo(expectedContent);
    }

    @Test
    public void testReadCodeFromQRCode() throws Exception {
        // Given
        String expectedContent = "https://example.com";
        BufferedImage qrCodeImage = CodesService.generateQRCode(expectedContent);

        // When
        String decodedContent = CodesService.readCode(qrCodeImage);

        // Then
        assertThat(decodedContent).isEqualTo(expectedContent);
    }

    @Test
    public void testReadCodeWithNullImage() {
        // Expect IllegalArgumentException when null image is passed
        assertThrows(IllegalArgumentException.class, () -> {
            CodesService.readCode(null);
        });
    }
}
