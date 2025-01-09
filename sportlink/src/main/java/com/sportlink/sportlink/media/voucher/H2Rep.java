package com.sportlink.sportlink.media.voucher;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class H2Rep implements I_VoucherMediaRep{

    private final JpaRepository<VoucherMedia, Long> repo;

    public H2Rep(JpaRepository<VoucherMedia, Long> repo) {
        this.repo = repo;
    }

    @Override
    public Optional<VoucherMedia> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public VoucherMedia save(VoucherMedia voucherMedia) {
        return repo.save(voucherMedia);
    }
}
