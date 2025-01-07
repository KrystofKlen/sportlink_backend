package com.sportlink.sportlink;

import com.sportlink.sportlink.codes.CodeData;
import com.sportlink.sportlink.codes.CodesService;
import com.sportlink.sportlink.codes.I_CodesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

}
