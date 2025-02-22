package voucher;

import com.sportlink.sportlink.account.account.I_AccountRepository;
import com.sportlink.sportlink.currency.I_CurrencyRepository;
import com.sportlink.sportlink.utils.ImgService;
import com.sportlink.sportlink.security.EncryptionUtil;
import com.sportlink.sportlink.utils.DTO_Adapter;
import com.sportlink.sportlink.voucher.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class VoucherServiceUT {

    @Mock
    private I_VoucherRepository voucherRepository;

    @Mock
    private I_CurrencyRepository currencyRepository;

    @Mock
    private I_AccountRepository accountRepository;

    @Mock
    private DTO_Adapter adapter;

    @Mock
    private ImgService imgService;

    @InjectMocks
    private VoucherService voucherService;

    @InjectMocks
    private EncryptionUtil encryptionUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getVoucher_ShouldReturnDTO_WhenVoucherExists() {
        Long voucherId = 1L;
        Voucher voucher = new Voucher();
        DTO_Voucher dtoVoucher = new DTO_Voucher();

        when(voucherRepository.findById(voucherId)).thenReturn(Optional.of(voucher));
        when(adapter.getDTO_Voucher(voucher)).thenReturn(dtoVoucher);

        Optional<DTO_Voucher> result = voucherService.getVoucher(voucherId);

        assertTrue(result.isPresent());
        assertEquals(dtoVoucher, result.get());
        verify(voucherRepository).findById(voucherId);
        verify(adapter).getDTO_Voucher(voucher);
    }

    @Test
    void getVoucher_ShouldReturnEmpty_WhenVoucherDoesNotExist() {
        Long voucherId = 1L;

        when(voucherRepository.findById(voucherId)).thenReturn(Optional.empty());

        Optional<DTO_Voucher> result = voucherService.getVoucher(voucherId);

        assertTrue(result.isEmpty());
        verify(voucherRepository).findById(voucherId);
        verifyNoInteractions(adapter);
    }

    @Test
    void testUpdateExpiredVouchers() {
        // Given
        LocalDate today = LocalDate.now();

        Voucher validVoucher = new Voucher();
        validVoucher.setState(VOUCHER_STATE.BOUGHT);
        validVoucher.setExpirationDate(today);
        Voucher expiredVoucher = new Voucher(); // Should expire
        expiredVoucher.setState(VOUCHER_STATE.IN_OFFER);
        expiredVoucher.setExpirationDate(today.minusDays(2));

        List<Voucher> vouchers = Arrays.asList(validVoucher, expiredVoucher);

        when(voucherRepository.findAll()).thenReturn(vouchers);

        // When
        voucherService.expireVouchers();

        // Then
        assertEquals(VOUCHER_STATE.EXPIRED, expiredVoucher.getState()); // Should be updated
        assertEquals(VOUCHER_STATE.BOUGHT, validVoucher.getState()); // Should remain unchanged
    }
}