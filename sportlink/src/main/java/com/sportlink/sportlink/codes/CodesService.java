package com.sportlink.sportlink.codes;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sportlink.sportlink.security.EncryptionUtil;
import jakarta.transaction.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.Optional;

@Service
public class CodesService {

    public static final int CODE_LENGTH = 10;

    @Autowired
    private final I_CodesRepository codesRepository;

    public CodesService(I_CodesRepository codesRepository) {
        this.codesRepository = codesRepository;
    }

    public Optional<BufferedImage> createQRCode(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
            return Optional.of(MatrixToImageWriter.toBufferedImage(bitMatrix));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<BufferedImage> createBarCode(String text, int width, int height) {
        try {
            com.google.zxing.oned.Code128Writer barCodeWriter = new com.google.zxing.oned.Code128Writer();
            BitMatrix bitMatrix = barCodeWriter.encode(text, BarcodeFormat.CODE_128, width, height);
            return Optional.of(MatrixToImageWriter.toBufferedImage(bitMatrix));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<String> getCode(BufferedImage image) {
        try {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(
                    new BufferedImageLuminanceSource(image)
            ));
            Result result = new MultiFormatReader().decode(bitmap);
            return Optional.ofNullable(result.getText());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<CodeData> findByCode(String code) {
        return codesRepository.findByCode(code);
    }

    @Transactional
    public CodeData saveCode(CodeData codeData) {
        return codesRepository.save(codeData);
    }

    @Transactional
    public boolean refreshCode(String code, Long exp) {
        Optional<CodeData> codeDataOpt = codesRepository.findByCode(code);
        if (codeDataOpt.isEmpty()) {
            return false;
        }
        CodeData codeData = codeDataOpt.get();
        codeData.setExp(exp);

        boolean newCodeFound = false;
        while (!newCodeFound) {
            codeData.setCode(EncryptionUtil.generateRandomSequence(CODE_LENGTH));
            try {
                saveCode(codeData);
                newCodeFound = true;
            } catch (ConstraintViolationException e){}
        }

        return true;
    }
}
