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
import com.sportlink.sportlink.account.Account;
import com.sportlink.sportlink.account.I_AccountRepository;
import com.sportlink.sportlink.redis.RedisService;
import com.sportlink.sportlink.security.EncryptionUtil;
import com.sportlink.sportlink.socket.WebSocketHandler;
import com.sportlink.sportlink.utils.EmailSender;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CodesService {

    public static final int EXPIRE_TIME_MIN = 1;
    private final RedisService redisService;
    private final WebSocketHandler webSocketHandler;
    private final I_AccountRepository accountRepository;
    private final EmailSender emailSender;

    // sends code to location, where it will be scanned by user
    public String sendLocationOTP(long locationId, long userId) {
        String code = EncryptionUtil.generateRandomSequence(10);
        ;
        String payload = Long.toString(userId);
        redisService.saveValueWithExpiration(code, payload, 1);
        // send to location
        webSocketHandler.sendCodeToLocation(locationId, code);
        return code;
    }

    // returns token to be used in link for password reset
    public String sendCodeForOTP(Long accountId) throws Exception {
        Account acc = accountRepository.findById(accountId).orElseThrow();
        String email = acc.getLoginEmail();
        String otp = EncryptionUtil.generateRandomSequence(10);
        emailSender.sendOtpPasswordChangeEmail(email, otp);
        String token = EncryptionUtil.generateRandomSequence(30);
        redisService.saveValueWithExpiration(token, otp, 2);
        return token;
    }

    public static Optional<BufferedImage> createQRCode(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
            return Optional.of(MatrixToImageWriter.toBufferedImage(bitMatrix));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<BufferedImage> createBarCode(String text, int width, int height) {
        try {
            com.google.zxing.oned.Code128Writer barCodeWriter = new com.google.zxing.oned.Code128Writer();
            BitMatrix bitMatrix = barCodeWriter.encode(text, BarcodeFormat.CODE_128, width, height);
            return Optional.of(MatrixToImageWriter.toBufferedImage(bitMatrix));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<String> getCode(BufferedImage image) {
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
}
