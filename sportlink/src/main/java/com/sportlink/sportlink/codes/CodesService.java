package com.sportlink.sportlink.codes;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import org.springframework.stereotype.Service;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@Service
public class CodesService {

    public BufferedImage generateBarCode(String content) throws Exception{
        int width = 300; // Barcode width
        int height = 100; // Barcode height

        // Generate the barcode matrix
        BitMatrix bitMatrix = new MultiFormatWriter().encode(
                content,
                BarcodeFormat.CODE_128,
                width,
                height
        );

        // Convert the BitMatrix to BufferedImage
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    public static BufferedImage generateQRCode(String content) throws Exception {
        int width = 300; // QR Code width
        int height = 300; // QR Code height

        // Define encoding hints
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1); // Minimal white border

        // Generate the QR Code matrix
        BitMatrix bitMatrix = new MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                width,
                height,
                hints
        );

        // Convert the BitMatrix to BufferedImage
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    // Reads data from barcode or qr code
    public static String readCode(BufferedImage image) throws Exception {
        if (image == null) {
            throw new IllegalArgumentException("BufferedImage cannot be null");
        }

        // Use ZXing to decode the image
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Result result = new MultiFormatReader().decode(bitmap);
        return result.getText();
    }
}
