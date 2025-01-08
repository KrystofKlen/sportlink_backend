package com.sportlink.sportlink.voucher;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface I_VoucherRepository {
    Voucher saveVoucher(Voucher voucher);
    Optional<Voucher> getVoucher(Long voucherId);
    void deleteVoucher(Long voucherId);
    Page<Voucher> findAllByRandomOrder(Pageable pageable);
}
