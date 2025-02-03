package com.sportlink.sportlink.voucher;

import com.sportlink.sportlink.account.account.I_AccountRepository;
import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.security.EncryptionUtil;
import com.sportlink.sportlink.transfer.I_TransferRepository;
import com.sportlink.sportlink.transfer.Transfer;
import com.sportlink.sportlink.utils.RESULT_CODE;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.sportlink.sportlink.utils.RESULT_CODE.*;

@Service
@AllArgsConstructor
@Slf4j
public class VoucherTransactionManager {

    private final I_VoucherRepository voucherRepository;
    private final I_TransferRepository transferRepository;
    private final I_AccountRepository accountRepository;

    @Transactional
    public RESULT_CODE buyVoucher(long voucherId, UserAccount userAccount) {
        Voucher voucher = voucherRepository.findById(voucherId).orElseThrow();

        if (voucher.getState() == VOUCHER_STATE.BOUGHT) {
            // voucher already redeemed
            return RESULT_CODE.VOUCHER_NOT_AVAILABLE;
        }
        int amountNeeded = voucher.getPrice();

        // check if user has enough money
        if(userAccount.getBalance().isEmpty() || !userAccount.getBalance().containsKey(voucher.getCurrency())){
            return RESULT_CODE.INSUFFICIENT_FUNDS;
        }
        int updatedAmount = userAccount.getBalance().get(voucher.getCurrency()) - amountNeeded;
        if (updatedAmount < 0) {
            return INSUFFICIENT_FUNDS;
        }
        // deduct amount
        userAccount.getBalance().put(voucher.getCurrency(), updatedAmount);
        accountRepository.save(userAccount);

        voucher.setBuyer(userAccount);

        Transfer transfer = new Transfer(null, userAccount, LocalDateTime.now(), voucher.getCurrency(), -voucher.getPrice());
        transferRepository.save(transfer);

        // update voucher
        voucher.setState(VOUCHER_STATE.BOUGHT);
        voucherRepository.save(voucher);

        log.info("Voucher bought: " + voucher.getId() + ", accountBuying: " + userAccount.getId());
        return BOUGHT;
    }

    @Transactional
    public RESULT_CODE redeemVoucher(RedeemRequest redeemRequest, long companyId, String otp) {
        Voucher voucher = voucherRepository.findById(redeemRequest.voucherId).orElseThrow();

        if(otp==null || !otp.equals(redeemRequest.otp)){
            return INVALID_CODE;
        }

        // check code matches
        try {
            String code = EncryptionUtil.decrypt(voucher.getCode());
            if(!code.equals(redeemRequest.voucherCode)){
                return INVALID_CODE;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // check account issuing voucher also requests redeem
        if(voucher.getIssuer().getId() != companyId){
            return VOUCHER_ISSUED_BY_ANOTHER_ISSUER;
        }

        // check correct voucher state
        if (voucher.getState() != VOUCHER_STATE.BOUGHT) {
            return WRONG_VOUCHER_STATE;
        }

        // redeem
        voucher.setState(VOUCHER_STATE.REDEEMED);
        voucherRepository.save(voucher);

        log.info("Voucher redeemed: " + voucher.getId() + ", accountRedeeming: " + companyId);
        return REDEEMED;
    }
}
