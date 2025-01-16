package voucher;

import com.sportlink.sportlink.SportlinkApplication;
import com.sportlink.sportlink.account.account.I_AccountRepository;
import com.sportlink.sportlink.account.company.CompanyAccount;
import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.currency.Currency;
import com.sportlink.sportlink.currency.I_CurrencyRepository;
import com.sportlink.sportlink.transfer.I_TransferRepository;
import com.sportlink.sportlink.utils.RESULT_CODE;
import com.sportlink.sportlink.voucher.I_VoucherRepository;
import com.sportlink.sportlink.voucher.RedeemTransactionManager;
import com.sportlink.sportlink.voucher.VOUCHER_STATE;
import com.sportlink.sportlink.voucher.Voucher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SportlinkApplication.class)
@Transactional
class RedeemManagerIT {

    @Autowired
    private RedeemTransactionManager redeemTransactionManager;

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
    void redeemVoucher_shouldRedeemVoucherWhenFundsAreSufficient() {
        RESULT_CODE result = redeemTransactionManager.redeemVoucher(testVoucher.getId(), testUserAccount);

        assertEquals(RESULT_CODE.REDEEMED, result);

        Voucher redeemedVoucher = voucherRepository.findById(testVoucher.getId()).orElseThrow();
        assertEquals(VOUCHER_STATE.REDEEMED, redeemedVoucher.getState());
        assertEquals(testUserAccount.getId(), redeemedVoucher.getBuyer().getId());

        UserAccount updatedUserAccount = (UserAccount) accountRepository.findById(testUserAccount.getId()).orElseThrow();
        assertEquals(100, updatedUserAccount.getBalance().get(testVoucher.getCurrency()));

        assertTrue(transferRepository.findAll().stream().anyMatch(transfer ->
                transfer.getReceiver().getId().equals(testUserAccount.getId()) &&
                        transfer.getAmount() == testVoucher.getPrice()
        ));
    }

    @Test
    void redeemVoucher_shouldReturnInsufficientFundsWhenBalanceIsLow() {
        testUserAccount.getBalance().put(testVoucher.getCurrency(), 50);
        accountRepository.save(testUserAccount);

        RESULT_CODE result = redeemTransactionManager.redeemVoucher(testVoucher.getId(), testUserAccount);

        assertEquals(RESULT_CODE.INSUFFICIENT_FUNDS, result);

        Voucher voucherAfterAttempt = voucherRepository.findById(testVoucher.getId()).orElseThrow();
        assertEquals(VOUCHER_STATE.IN_OFFER, voucherAfterAttempt.getState());

        UserAccount updatedUserAccount = (UserAccount) accountRepository.findById(testUserAccount.getId()).orElseThrow();
        assertEquals(50, updatedUserAccount.getBalance().get(testVoucher.getCurrency()));

        assertTrue(transferRepository.findAll().isEmpty());
    }

    @Test
    void redeemVoucher_shouldThrowExceptionWhenVoucherNotFound() {
        long nonExistentVoucherId = 999L;

        assertThrows(Exception.class, () ->
                redeemTransactionManager.redeemVoucher(nonExistentVoucherId, testUserAccount)
        );

        assertTrue(transferRepository.findAll().isEmpty());
        assertEquals(200, testUserAccount.getBalance().get(testVoucher.getCurrency()));
    }
}
