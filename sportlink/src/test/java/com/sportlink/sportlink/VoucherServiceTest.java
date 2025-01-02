package com.sportlink.sportlink;

import com.sportlink.sportlink.utils.DTO_Adapter;
import com.sportlink.sportlink.voucher.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VoucherServiceTest {

    @Mock
    private I_VoucherRepository voucherRepository;

    @Mock
    private DTO_Adapter adapter;

    @InjectMocks
    private VoucherService voucherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveVoucher_ShouldSaveVoucherAndReturnDTO() {
        Voucher voucher = new Voucher();
        voucher.setItem(new Item());
        Voucher savedVoucher = new Voucher();
        savedVoucher.setId(1L);
        savedVoucher.setItem(new Item());
        DTO_Voucher dtoVoucher = new DTO_Voucher();

        when(voucherRepository.saveVoucher(any(Voucher.class))).thenReturn(savedVoucher);
        when(adapter.getDTO_Voucher(savedVoucher)).thenReturn(dtoVoucher);

        DTO_Voucher result = voucherService.saveVoucher(voucher);

        assertNotNull(result);
        verify(voucherRepository).saveVoucher(any(Voucher.class));
        verify(adapter).getDTO_Voucher(savedVoucher);
    }

    @Test
    void getVoucher_ShouldReturnDTO_WhenVoucherExists() {
        Long voucherId = 1L;
        Voucher voucher = new Voucher();
        voucher.setId(voucherId);
        DTO_Voucher dtoVoucher = new DTO_Voucher();

        when(voucherRepository.getVoucher(voucherId)).thenReturn(Optional.of(voucher));
        when(adapter.getDTO_Voucher(voucher)).thenReturn(dtoVoucher);

        Optional<DTO_Voucher> result = voucherService.getVoucher(voucherId);

        assertTrue(result.isPresent());
        verify(voucherRepository).getVoucher(voucherId);
        verify(adapter).getDTO_Voucher(voucher);
    }

    @Test
    void getVoucher_ShouldReturnEmpty_WhenVoucherDoesNotExist() {
        Long voucherId = 1L;

        when(voucherRepository.getVoucher(voucherId)).thenReturn(Optional.empty());

        Optional<DTO_Voucher> result = voucherService.getVoucher(voucherId);

        assertTrue(result.isEmpty());
        verify(voucherRepository).getVoucher(voucherId);
        verifyNoInteractions(adapter);
    }

    @Test
    void updateVoucher_ShouldUpdateAndReturnVoucher() {
        Voucher existingVoucher = new Voucher();
        existingVoucher.setId(1L);
        existingVoucher.setExpirationDate(LocalDate.now());

        Voucher updatedVoucher = new Voucher();
        updatedVoucher.setId(1L);
        LocalDate today = LocalDate.now();
        LocalDate newExpirationDate = today.plusDays(10); // Add 10 days to the current date
        updatedVoucher.setExpirationDate(newExpirationDate);


        when(voucherRepository.getVoucher(1L)).thenReturn(Optional.of(existingVoucher));
        when(voucherRepository.saveVoucher(existingVoucher)).thenReturn(updatedVoucher);

        Voucher result = voucherService.updateVoucher(updatedVoucher);

        assertEquals(newExpirationDate, result.getExpirationDate());
        verify(voucherRepository).getVoucher(1L);
        verify(voucherRepository).saveVoucher(existingVoucher);
    }

    @Test
    void updateVoucher_ShouldThrowException_WhenVoucherDoesNotExist() {
        Voucher inputVoucher = new Voucher();
        inputVoucher.setId(99L);

        when(voucherRepository.getVoucher(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            voucherService.updateVoucher(inputVoucher);
        });

        assertEquals("Voucher not found with ID: 99", exception.getMessage());
        verify(voucherRepository).getVoucher(99L);
        verify(voucherRepository, never()).saveVoucher(any());
    }

    @Test
    void deleteVoucher_ShouldDeleteVoucher() {
        Long voucherId = 1L;

        doNothing().when(voucherRepository).deleteVoucher(voucherId);

        voucherService.deleteVoucher(voucherId);

        verify(voucherRepository).deleteVoucher(voucherId);
    }
}
