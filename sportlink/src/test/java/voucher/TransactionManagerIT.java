package voucher;

import com.sportlink.sportlink.SportlinkApplication;
import com.sportlink.sportlink.account.account.I_AccountRepository;
import com.sportlink.sportlink.account.company.CompanyAccount;
import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.currency.Currency;
import com.sportlink.sportlink.currency.I_CurrencyRepository;
import com.sportlink.sportlink.security.EncryptionUtil;
import com.sportlink.sportlink.transfer.I_TransferRepository;
import com.sportlink.sportlink.utils.RESULT_CODE;
import com.sportlink.sportlink.voucher.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(classes = SportlinkApplication.class)
@Transactional
class TransactionManagerIT {

    @Autowired
    private VoucherTransactionManager voucherTransactionManager;

    @Autowired
    private I_VoucherRepository voucherRepository;

    @Autowired
    private I_TransferRepository transferRepository;

    @Autowired
    private I_AccountRepository accountRepository;

    private Voucher testVoucher;
    private UserAccount testUserAccount;
    @Autowired
    private I_CurrencyRepository i_CurrencyRepository;

    @BeforeEach
    void setUp() {
        CompanyAccount companyAccount = new CompanyAccount();
        companyAccount.setName("company");
        companyAccount = accountRepository.save(companyAccount);


        Currency currency = new Currency();
        currency.setName("TEST");
        currency.setIssuer(companyAccount);
        currency = i_CurrencyRepository.save(currency);

        // Set up voucher
        testVoucher = new Voucher();
        testVoucher.setCurrency(currency);
        testVoucher.setPrice(100);
        testVoucher.setState(VOUCHER_STATE.IN_OFFER);
        testVoucher.setIssuer(companyAccount);
        testVoucher = voucherRepository.save(testVoucher);

        // Set up user account with sufficient balance
        HashMap<Currency, Integer> balance = new HashMap<>();
        balance.put(currency, 200);
        testUserAccount = new UserAccount();
        testUserAccount.setUsername("testUser");
        testUserAccount.setBalance(balance);
        testUserAccount = accountRepository.save(testUserAccount);
    }

    @Test
    void buyVoucher_shouldbuyVoucherWhenFundsAreSufficient() {
        RESULT_CODE result = voucherTransactionManager.buyVoucher(testVoucher.getId(), testUserAccount);

        assertEquals(RESULT_CODE.BOUGHT, result);

        Voucher boughtVoucher = voucherRepository.findById(testVoucher.getId()).orElseThrow();
        assertEquals(VOUCHER_STATE.BOUGHT, boughtVoucher.getState());
        assertEquals(testUserAccount.getId(), boughtVoucher.getBuyer().getId());

        UserAccount updatedUserAccount = (UserAccount) accountRepository.findById(testUserAccount.getId()).orElseThrow();
        assertEquals(100, updatedUserAccount.getBalance().get(testVoucher.getCurrency()));

        assertTrue(transferRepository.findAll().stream().anyMatch(transfer ->
                transfer.getUser().getId().equals(testUserAccount.getId()) &&
                        transfer.getAmount() == -testVoucher.getPrice()
        ));
    }

    @Test
    void buyVoucher_shouldReturnInsufficientFundsWhenBalanceIsLow() {
        testUserAccount.getBalance().put(testVoucher.getCurrency(), 50);
        accountRepository.save(testUserAccount);

        RESULT_CODE result = voucherTransactionManager.buyVoucher(testVoucher.getId(), testUserAccount);

        assertEquals(RESULT_CODE.INSUFFICIENT_FUNDS, result);

        Voucher voucherAfterAttempt = voucherRepository.findById(testVoucher.getId()).orElseThrow();
        assertEquals(VOUCHER_STATE.IN_OFFER, voucherAfterAttempt.getState());

        UserAccount updatedUserAccount = (UserAccount) accountRepository.findById(testUserAccount.getId()).orElseThrow();
        assertEquals(50, updatedUserAccount.getBalance().get(testVoucher.getCurrency()));

        assertTrue(transferRepository.findAll().isEmpty());
    }

    @Test
    void buyVoucher_shouldThrowExceptionWhenVoucherNotFound() {
        long nonExistentVoucherId = 999L;

        assertThrows(Exception.class, () ->
                voucherTransactionManager.buyVoucher(nonExistentVoucherId, testUserAccount)
        );

        assertTrue(transferRepository.findAll().isEmpty());
        assertEquals(200, testUserAccount.getBalance().get(testVoucher.getCurrency()));
    }

    @Test
    void buyVoucherOnBUY_Voucher() {
        testVoucher.setState(VOUCHER_STATE.BOUGHT);
        testVoucher = voucherRepository.save(testVoucher);
        RESULT_CODE result = voucherTransactionManager.buyVoucher(testVoucher.getId(), testUserAccount);
        assertEquals(RESULT_CODE.VOUCHER_NOT_AVAILABLE, result);
    }

    @Test
    void redeemVoucher_shouldRedeemWhenValid() {
        testVoucher.setState(VOUCHER_STATE.BOUGHT);
        try {
            testVoucher.setCode(EncryptionUtil.encrypt("validCode"));
        } catch (Exception e) {
            fail();
        }
        testVoucher = voucherRepository.save(testVoucher);

        RedeemRequest redeemRequest = new RedeemRequest(testVoucher.getId(), "validCode","1234");
        RESULT_CODE result = voucherTransactionManager.redeemVoucher(redeemRequest, testVoucher.getIssuer().getId(),"1234");

        assertEquals(RESULT_CODE.REDEEMED, result);
        assertEquals(VOUCHER_STATE.REDEEMED, voucherRepository.findById(testVoucher.getId()).orElseThrow().getState());
    }

    @Test
    void redeemVoucher_shouldReturnInvalidCodeWhenCodeDoesNotMatch() {
        testVoucher.setState(VOUCHER_STATE.BOUGHT);
        try {
            testVoucher.setCode(EncryptionUtil.encrypt("validCode"));
        } catch (Exception e) {
            fail();
        }
        testVoucher = voucherRepository.save(testVoucher);

        RedeemRequest redeemRequest = new RedeemRequest(testVoucher.getId(), "wrongCode","1234");
        RESULT_CODE result = voucherTransactionManager.redeemVoucher(redeemRequest, testVoucher.getIssuer().getId(),"1234");

        assertEquals(RESULT_CODE.INVALID_CODE, result);
        assertEquals(VOUCHER_STATE.BOUGHT, voucherRepository.findById(testVoucher.getId()).orElseThrow().getState());
    }

    @Test
    void redeemVoucher_shouldReturnVoucherIssuedByAnotherIssuer() {
        testVoucher.setState(VOUCHER_STATE.BOUGHT);
        try {
            testVoucher.setCode(EncryptionUtil.encrypt("validCode"));
        } catch (Exception e) {
            fail();
        }
        testVoucher = voucherRepository.save(testVoucher);

        long differentCompanyId = testVoucher.getIssuer().getId() + 1;
        RedeemRequest redeemRequest = new RedeemRequest(testVoucher.getId(), "validCode","1234");
        RESULT_CODE result = voucherTransactionManager.redeemVoucher(redeemRequest, differentCompanyId,"1234");

        assertEquals(RESULT_CODE.VOUCHER_ISSUED_BY_ANOTHER_ISSUER, result);
        assertEquals(VOUCHER_STATE.BOUGHT, voucherRepository.findById(testVoucher.getId()).orElseThrow().getState());
    }

    @Test
    void redeemVoucher_shouldReturnWrongVoucherStateWhenNotBought() {
        testVoucher.setState(VOUCHER_STATE.IN_OFFER);
        try {
            testVoucher.setCode(EncryptionUtil.encrypt("validCode"));
        } catch (Exception e) {
            fail();
        }
        testVoucher = voucherRepository.save(testVoucher);

        RedeemRequest redeemRequest = new RedeemRequest(testVoucher.getId(), "validCode","1234");
        RESULT_CODE result = voucherTransactionManager.redeemVoucher(redeemRequest, testVoucher.getIssuer().getId(),"1234");

        assertEquals(RESULT_CODE.WRONG_VOUCHER_STATE, result);
        assertEquals(VOUCHER_STATE.IN_OFFER, voucherRepository.findById(testVoucher.getId()).orElseThrow().getState());
    }

    @Test
    void redeemVoucher_shouldThrowExceptionWhenVoucherNotFound() {
        long nonExistentVoucherId = 999L;
        RedeemRequest redeemRequest = new RedeemRequest(nonExistentVoucherId, "someCode","1234");

        assertThrows(Exception.class, () ->
                voucherTransactionManager.redeemVoucher(redeemRequest, testUserAccount.getId(),"1234")
        );
    }

}
