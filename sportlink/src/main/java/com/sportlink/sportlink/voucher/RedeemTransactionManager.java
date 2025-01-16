package com.sportlink.sportlink.voucher;

import com.sportlink.sportlink.account.account.I_AccountRepository;
import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.transfer.I_TransferRepository;
import com.sportlink.sportlink.transfer.Transfer;
import com.sportlink.sportlink.utils.RESULT_CODE;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.sportlink.sportlink.utils.RESULT_CODE.INSUFFICIENT_FUNDS;
import static com.sportlink.sportlink.utils.RESULT_CODE.REDEEMED;

@Service
@AllArgsConstructor
public class RedeemTransactionManager {

    private final I_VoucherRepository voucherRepository;
    private final I_TransferRepository transferRepository;
    private final I_AccountRepository accountRepository;

    @Transactional
    public RESULT_CODE redeemVoucher(long voucherId, UserAccount userAccount) {
        Voucher voucher = voucherRepository.findById(voucherId).orElseThrow();
        String currencyNeeded = voucher.getCurrency().getName();
        int amountNeeded = voucher.getPrice();

        // check if user has enough money
        int updatedAmount = userAccount.getBalance().get(voucher.getCurrency()) - amountNeeded;
        if (updatedAmount < 0) {
            return INSUFFICIENT_FUNDS;
        }
        // deduct amount
        userAccount.getBalance().put(voucher.getCurrency(), updatedAmount);
        accountRepository.save(userAccount);

        voucher.setBuyer(userAccount);

        Transfer transfer = new Transfer(null, userAccount, LocalDateTime.now(), voucher.getCurrency(), voucher.getPrice());
        transferRepository.save(transfer);

        // update voucher
        voucher.setState(VOUCHER_STATE.REDEEMED);
        voucherRepository.save(voucher);

        return REDEEMED;
    }
}
