package com.sportlink.sportlink.voucher;

import java.util.Optional;

public interface I_VoucherRepository {
    Voucher saveVoucher(Voucher voucher);
    Optional<Voucher> getVoucher(Long voucherId);
    void deleteVoucher(Long voucherId);
}
