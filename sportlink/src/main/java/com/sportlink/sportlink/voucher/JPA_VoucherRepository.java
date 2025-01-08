package com.sportlink.sportlink.voucher;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JPA_VoucherRepository extends JpaRepository<Voucher, Long> {
    @Query("SELECT v FROM Voucher v WHERE v.state = com.sportlink.sportlink.voucher.VOUCHER_STATE.IN_OFFER ORDER BY RANDOM()")
    Page<Voucher> findAllByRandomOrder(Pageable pageable);
}
