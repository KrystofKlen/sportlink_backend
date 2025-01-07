package voucher;

import com.sportlink.sportlink.SportlinkApplication;
import com.sportlink.sportlink.account.Account;
import com.sportlink.sportlink.currency.Currency;
import com.sportlink.sportlink.security.EncryptionUtil;
import com.sportlink.sportlink.voucher.DTO_Voucher;
import com.sportlink.sportlink.voucher.VOUCHER_STATE;
import com.sportlink.sportlink.voucher.Voucher;
import com.sportlink.sportlink.voucher.VoucherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(classes = SportlinkApplication.class)
public class VoucherServiceIT {

    @Autowired
    private VoucherService voucherService;

    private Voucher voucher;

    @BeforeEach
    void setUp() {
        Account account = new Account();
        Currency currency = new Currency();
        currency.setName(EncryptionUtil.generateRandomSequence(20));
        currency.setIssuer(account);

        voucher = new Voucher(
                null,
                "title",
                "description",
                account,
                currency,
                10,
                LocalDate.now().plusDays(30),
                VOUCHER_STATE.NOT_IN_OFFER,
                "ABCD"
        );
        voucher.setIssuer(account);

        voucher.setCurrency(currency);
        voucher.setPrice(10);
        voucher.setState(VOUCHER_STATE.NOT_IN_OFFER);
        voucher.setExpirationDate(LocalDate.now().plusDays(30));
        voucher.setCode("ABC123");
    }

    @Test
    void testSaveVoucher() {
        DTO_Voucher savedVoucher = voucherService.saveVoucher(voucher);
        assertThat(savedVoucher).isNotNull();
        assertEquals(savedVoucher.getTitle(), voucher.getTitle());
        assertEquals(savedVoucher.getDescription(), voucher.getDescription());
    }

    @Test
    void testGetVoucher() {
        DTO_Voucher savedVoucher = voucherService.saveVoucher(voucher);
        Optional<DTO_Voucher> fetchedVoucher = voucherService.getVoucher(savedVoucher.getId());
        assertThat(fetchedVoucher).isPresent();
        assertEquals(savedVoucher.getId(), fetchedVoucher.get().getId());
        assertEquals(savedVoucher.getPrice(), fetchedVoucher.get().getPrice());
        assertEquals( "title", fetchedVoucher.get().getTitle() );
    }

    @Test
    void testUpdateVoucher() {
        DTO_Voucher savedVoucher = voucherService.saveVoucher(voucher);
        Voucher updatedVoucher = new Voucher();
        updatedVoucher.setId(savedVoucher.getId());
        updatedVoucher.setState(VOUCHER_STATE.REDEEMED);
        Voucher result = voucherService.updateVoucher(updatedVoucher);
        assertEquals("title", result.getTitle());
        assertEquals("description", result.getDescription());
        assertEquals(savedVoucher.getId(), result.getId());
        assertEquals(savedVoucher.getPrice(), result.getPrice());
        assertEquals(result.getState(), VOUCHER_STATE.REDEEMED);
    }

    @Test
    void testDeleteVoucher() {
        DTO_Voucher savedVoucher = voucherService.saveVoucher(voucher);
        voucherService.deleteVoucher(savedVoucher.getId());
        Optional<DTO_Voucher> fetchedVoucher = voucherService.getVoucher(savedVoucher.getId());
        assertThat(fetchedVoucher).isEmpty();
    }
}
