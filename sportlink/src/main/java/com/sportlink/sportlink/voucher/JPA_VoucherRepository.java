package com.sportlink.sportlink.voucher;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JPA_VoucherRepository extends JpaRepository<Voucher, Long> {
}
