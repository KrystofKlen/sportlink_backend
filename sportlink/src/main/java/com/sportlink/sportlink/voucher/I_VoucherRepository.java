package com.sportlink.sportlink.voucher;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface I_VoucherRepository {
    Voucher saveVoucher(Voucher voucher);
    Optional<Voucher> getVoucher(Long voucherId);
    void deleteVoucher(Long voucherId);
}
