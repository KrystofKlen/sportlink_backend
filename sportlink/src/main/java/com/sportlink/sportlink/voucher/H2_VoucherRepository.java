package com.sportlink.sportlink.voucher;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
public class H2_VoucherRepository implements I_VoucherRepository {

    private final JPA_VoucherRepository jpaRepository;

    public H2_VoucherRepository(JPA_VoucherRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Voucher saveVoucher(Voucher voucher) {
        return jpaRepository.save(voucher);
    }

    @Override
    public Optional<Voucher> getVoucher(Long voucherId) {
        return jpaRepository.findById(voucherId);
    }

    @Override
    public void deleteVoucher(Long voucherId) {
        jpaRepository.deleteById(voucherId);
    }
}
