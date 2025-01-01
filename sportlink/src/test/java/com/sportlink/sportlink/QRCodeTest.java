package com.sportlink.sportlink;

import com.sportlink.sportlink.codes.QRCode;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class QRCodeTest {
    @Test
    public void testQRCodeGenerationAndDecoding() {
        try {
            // Create a ComplexData object to be encoded
            QRCodeTestObj originalData = new QRCodeTestObj(
                    "Hello",
                    "World",
                    System.currentTimeMillis() + 24 * 60 * 60 * 1000L,
                    123456789L
            );

            // Create a QRCode object and generate QR code
            QRCode<QRCodeTestObj> qrCode = new QRCode<>(originalData);
            BufferedImage qrImage = qrCode.generateQRCode();

            // Save the QR code image to a file (for testing purposes, you can skip saving if you prefer)
            File qrFile = new File("qr_test.png");
            ImageIO.write(qrImage, "PNG", qrFile);

            // Decode the QR code back into the original object
            QRCodeTestObj decodedData = qrCode.decodeQRCode(qrImage);

            // Assert that the decoded object is the same as the original object
            assertNotNull(decodedData); // Ensure the decoded data is not null
            assertEquals(originalData, decodedData); // Compare the objects using equals()
            System.out.printf("Decoded data: %s\n", decodedData);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
