package com.sportlink.sportlink;

import com.sportlink.sportlink.codes.CodeData;
import com.sportlink.sportlink.codes.CodesService;
import com.sportlink.sportlink.codes.I_CodesRepository;
import com.sportlink.sportlink.security.EncryptionUtil;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.awt.image.BufferedImage;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CodesServiceTest {

    @Mock
    private I_CodesRepository codesRepository;

    @InjectMocks
    private CodesService codesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateQRCode_success() {
        // Setup
        String text = "test qr code";
        int width = 200;
        int height = 200;

        // Act
        Optional<BufferedImage> result = codesService.createQRCode(text, width, height);

        // Assert
        assertTrue(result.isPresent());
    }

    @Test
    void testCreateQRCode_failure() {
        // Setup
        String text = "";
        int width = 200;
        int height = 200;

        // Act
        Optional<BufferedImage> result = codesService.createQRCode(text, width, height);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testCreateBarCode_success() {
        // Setup
        String text = "test barcode";
        int width = 200;
        int height = 100;

        // Act
        Optional<BufferedImage> result = codesService.createBarCode(text, width, height);

        // Assert
        assertTrue(result.isPresent());
    }

    @Test
    void testCreateBarCode_failure() {
        // Setup
        String text = "";
        int width = 200;
        int height = 100;

        // Act
        Optional<BufferedImage> result = codesService.createBarCode(text, width, height);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetCode_success() {
        // Setup
        String text = "test code";
        BufferedImage imageQR = codesService.createQRCode(text,200,200).get();
        BufferedImage imageBar = codesService.createBarCode(text,200,100).get();

        // Act
        Optional<String> resultQR = codesService.getCode(imageQR);
        Optional<String> resultBar = codesService.getCode(imageBar);

        // Assert
        assertTrue(resultQR.isPresent());
        assertEquals(text, resultQR.get());
        assertTrue(resultBar.isPresent());
        assertEquals(text, resultBar.get());
    }

    @Test
    void testGetCode_failure() {
        // Setup
        BufferedImage image = mock(BufferedImage.class);

        // Act
        Optional<String> result = codesService.getCode(image);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByCode_success() {
        // Setup
        String code = "test_code";
        CodeData codeData = new CodeData();
        when(codesRepository.findByCode(code)).thenReturn(Optional.of(codeData));

        // Act
        Optional<CodeData> result = codesService.findByCode(code);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(codeData, result.get());
    }

    @Test
    void testFindByCode_failure() {
        // Setup
        String code = "non_existent_code";
        when(codesRepository.findByCode(code)).thenReturn(Optional.empty());

        // Act
        Optional<CodeData> result = codesService.findByCode(code);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testSaveCode() {
        // Setup
        CodeData codeData = new CodeData();

        // Act
        codesService.saveCode(codeData);

        // Assert
        verify(codesRepository, times(1)).save(codeData);
    }

    @Test
    void testRefreshCode_success() {
        // Setup
        String code = "existing_code";
        Long exp = 123456789L;
        CodeData codeData = new CodeData();
        codeData.setCode(code);
        when(codesRepository.findByCode(code)).thenReturn(Optional.of(codeData));
        when(codesRepository.save(codeData)).thenReturn(codeData);

        // Mock EncryptionUtil.generateRandomSequence to return a predictable sequence
        try (MockedStatic<EncryptionUtil> encryptionUtilMockedStatic = mockStatic(EncryptionUtil.class)) {
            encryptionUtilMockedStatic.when(() -> EncryptionUtil.generateRandomSequence(CodesService.CODE_LENGTH))
                    .thenReturn("new_unique_code");

            // Act
            boolean result = codesService.refreshCode(code, exp);

            // Assert
            assertTrue(result);
            assertEquals("new_unique_code", codeData.getCode());
        }
    }

    @Test
    void testRefreshCode_retryOnConstraintViolation() {
        // Setup
        String code = "existing_code";
        Long exp = 123456789L;
        CodeData codeData = new CodeData();
        codeData.setCode(code);
        when(codesRepository.findByCode(code)).thenReturn(Optional.of(codeData));

        // Simulate a constraint violation on the first try, followed by success on the second try
        when(codesRepository.save(codeData))
                .thenThrow(new ConstraintViolationException("Duplicate code", null, null))
                .thenReturn(codeData);

        // Mock EncryptionUtil.generateRandomSequence to return a predictable sequence
        try (MockedStatic<EncryptionUtil> encryptionUtilMockedStatic = mockStatic(EncryptionUtil.class)) {
            encryptionUtilMockedStatic.when(() -> EncryptionUtil.generateRandomSequence(CodesService.CODE_LENGTH))
                    .thenReturn("new_unique_code");

            // Act
            boolean result = codesService.refreshCode(code, exp);

            // Assert
            assertTrue(result);
            assertEquals("new_unique_code", codeData.getCode());
        }
    }
}
