package com.sportlink.sportlink.codes;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import lombok.AllArgsConstructor;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class QRCode<T extends Serializable> {

    private final T data;


    public BufferedImage generateQRCode() throws Exception {
        String content = serializeToBase64(data); // Serialize object and convert to Base64

        int width = 300; // QR Code width
        int height = 300; // QR Code height

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1); // Minimal white border

        BitMatrix bitMatrix = new MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                width,
                height,
                hints
        );

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    public T decodeQRCode(BufferedImage qrCodeImage) throws Exception {
        String qrCodeContent = decodeQRCodeContentFromImage(qrCodeImage); // Decode the Base64 content from the QR code
        return deserializeFromBase64(qrCodeContent); // Decode Base64 and deserialize
    }

    /**
     * Converts an object to a Base64 encoded string via serialization.
     */
    private String serializeToBase64(T object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);
        }
        byte[] byteData = byteArrayOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(byteData); // Base64 encode
    }

    /**
     * Converts a Base64 string back into an object via deserialization.
     */
    private T deserializeFromBase64(String base64Data) {
        byte[] byteData = Base64.getDecoder().decode(base64Data);
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byteData))) {
            return (T) objectInputStream.readObject(); // Deserialize object
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing the object", e);
        }
    }

    /**
     * Decodes the Base64 content embedded in the QR code from a BufferedImage.
     */
    private String decodeQRCodeContentFromImage(BufferedImage qrCodeImage) throws Exception {
        // Create a ZXing reader for the QR Code
        QRCodeReader qrCodeReader = new QRCodeReader();

        // Convert BufferedImage to LuminanceSource (ZXing's format for images)
        LuminanceSource source = new BufferedImageLuminanceSource(qrCodeImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        // Decode the image and extract the content
        Result result = qrCodeReader.decode(bitmap);
        return result.getText(); // The result contains the Base64 string
    }
}
