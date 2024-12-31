package com.sportlink.sportlink.security;

import org.springframework.context.annotation.Bean;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;

public class EncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static SecretKey secretKey;


    public static void initializeKey(String secretKeyString) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(secretKeyString);
            secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to decode secretKeyString", e);
        }
    }

    public static String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }

    // SaltGenerator class can be an inner static class or a separate class
    public static class SaltGenerator {
        private final int saltLength;

        public SaltGenerator(int saltLength) {
            this.saltLength = saltLength;
        }

        public String generateSalt() {
            SecureRandom random = new SecureRandom();
            byte[] saltBytes = new byte[saltLength];
            random.nextBytes(saltBytes);
            return Base64.getEncoder().encodeToString(saltBytes);
        }
    }
}
