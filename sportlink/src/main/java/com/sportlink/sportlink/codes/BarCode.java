package com.sportlink.sportlink.codes;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import lombok.AllArgsConstructor;

import java.awt.image.BufferedImage;

@AllArgsConstructor
public class BarCode {

    private final String content;

    public static BufferedImage generateBarCode(String content) throws Exception{
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

    public static String readBarCode(BufferedImage image) throws Exception {
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
