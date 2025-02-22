package voucher;

import com.sportlink.sportlink.SportlinkApplication;
import com.sportlink.sportlink.account.ROLE;
import com.sportlink.sportlink.account.account.I_AccountRepository;
import com.sportlink.sportlink.account.company.CompanyAccount;
import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.currency.Currency;
import com.sportlink.sportlink.currency.I_CurrencyRepository;
import com.sportlink.sportlink.security.EncryptionUtil;
import com.sportlink.sportlink.voucher.DTO_Voucher;
import com.sportlink.sportlink.voucher.I_VoucherRepository;
import com.sportlink.sportlink.voucher.Voucher;
import com.sportlink.sportlink.voucher.VoucherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(classes = SportlinkApplication.class)
public class VoucherServiceIT {

    @Autowired
    private VoucherService voucherService;
    @Autowired
    private I_AccountRepository accountRepository;
    @Autowired
    private I_CurrencyRepository currencyRepository;

    @Autowired
    private EncryptionUtil encryptionUtil;;

    private CompanyAccount issuerAccount;
    private Currency currency;
    @Autowired
    private I_VoucherRepository i_VoucherRepository;

    @BeforeEach
    void setUp() {
        // Setup test data
        issuerAccount = new CompanyAccount();
        issuerAccount.setName("issuer");
        issuerAccount.setRole(ROLE.ROLE_COMPANY);
        issuerAccount = accountRepository.save(issuerAccount);

        currency = new Currency();
        currency.setName("TestCurrency");
        currency.setIssuer(issuerAccount);
        currencyRepository.save(currency);
    }

    @Test
    void testAddVoucher() throws Exception {
        // Prepare input data
        DTO_Voucher dtoVoucher = new DTO_Voucher();
        dtoVoucher.setTitle("Test Voucher");
        dtoVoucher.setDescription("Integration Test");
        dtoVoucher.setPrice(100);
        dtoVoucher.setExpirationDate(LocalDate.now().plusDays(1));
        dtoVoucher.setCode("TESTCODE123");

        // Call service method
        DTO_Voucher result = voucherService.addVoucher(issuerAccount.getId(), dtoVoucher, List.of());

        // Assertions
        assertThat(result).isNotNull();
        assertEquals("Test Voucher", result.getTitle());
        assertEquals("Integration Test", result.getDescription());
    }

    @Test
    void testGetVoucher() throws Exception {
        // Add a voucher first
        DTO_Voucher dtoVoucher = new DTO_Voucher();
        dtoVoucher.setTitle("Test Voucher");
        dtoVoucher.setDescription("Integration Test");
        dtoVoucher.setPrice(100);
        dtoVoucher.setExpirationDate(LocalDate.now().plusDays(1));
        dtoVoucher.setCode("TESTCODE123");

        DTO_Voucher savedVoucher = voucherService.addVoucher(issuerAccount.getId(), dtoVoucher, List.of());

        // Fetch the voucher
        Optional<DTO_Voucher> fetchedVoucher = voucherService.getVoucher(savedVoucher.getId());

        // Assertions
        assertThat(fetchedVoucher).isPresent();
        assertEquals("Test Voucher", fetchedVoucher.get().getTitle());
        assertEquals("Integration Test", fetchedVoucher.get().getDescription());
    }


    @Test
    void testDeleteVoucher() throws Exception {
        // Add a voucher first
        DTO_Voucher dtoVoucher = new DTO_Voucher();
        dtoVoucher.setTitle("Test Voucher");
        dtoVoucher.setDescription("Integration Test");
        dtoVoucher.setPrice(100);
        dtoVoucher.setExpirationDate(LocalDate.now().plusDays(1));
        dtoVoucher.setCode("TESTCODE123");

        DTO_Voucher savedVoucher = voucherService.addVoucher(issuerAccount.getId(), dtoVoucher, List.of());

        // Delete the voucher
        voucherService.deleteVoucher(savedVoucher.getId(), issuerAccount.getId());

        // Verify it no longer exists
        Optional<DTO_Voucher> fetchedVoucher = voucherService.getVoucher(savedVoucher.getId());
        assertThat(fetchedVoucher).isEmpty();
    }

    @Test
    void testGetVouchersInOffer() throws Exception {
        // Add a voucher first
        DTO_Voucher dtoVoucher = new DTO_Voucher();
        dtoVoucher.setTitle("Test Voucher");
        dtoVoucher.setDescription("Integration Test");
        dtoVoucher.setPrice(100);
        dtoVoucher.setExpirationDate(LocalDate.now().plusDays(1));
        dtoVoucher.setCode("TESTCODE123");

        voucherService.addVoucher(issuerAccount.getId(), dtoVoucher, List.of());

        // Fetch vouchers in offer
        List<DTO_Voucher> vouchersInOffer = voucherService.getVouchersInOffer(0, 10).getContent();

        // Assertions
        assertThat(vouchersInOffer).isNotEmpty();
        assertThat(vouchersInOffer.get(0).getTitle()).isEqualTo("Test Voucher");
    }

    @Test
    void testRevealVoucherCode(){
        UserAccount buyer = new UserAccount();
        buyer.setUsername("buyer");
        buyer.setPassword("password");
        buyer = accountRepository.save(buyer);

        Voucher voucher = new Voucher();
        voucher.setTitle("Test Voucher");
        voucher.setDescription("Integration Test");
        voucher.setPrice(100);
        try {
            String codeEncrypted = EncryptionUtil.encrypt("ABCD");
            voucher.setCode(codeEncrypted);
        } catch (Exception e) {
            fail();
        }
        voucher.setIssuer(issuerAccount);
        voucher = i_VoucherRepository.save(voucher);
        try {
            voucherService.revealCode( voucher.getId(), buyer.getId());
            fail();
        }catch (Exception ignored){}

        voucher.setBuyer(buyer);
        voucher = i_VoucherRepository.save(voucher);
        try {
            voucherService.revealCode( voucher.getId(), buyer.getId() + 1);
            fail();
        }catch (Exception ignored){}
        try {
            String code = voucherService.revealCode( voucher.getId(), buyer.getId());
            assertEquals("ABCD", code);
        }catch (Exception e){
            fail();
        }
    }

}

