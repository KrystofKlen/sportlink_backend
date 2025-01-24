package com.sportlink.sportlink.voucher;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.sportlink.sportlink.voucher.VOUCHER_STATE.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface I_VoucherRepository extends JpaRepository<Voucher, Long> {

    @Query("SELECT v FROM Voucher v WHERE v.state =:state ")
    Page<Voucher> findByState(Pageable pageable, @Param("state") VOUCHER_STATE state);

    @Query("SELECT v FROM Voucher v WHERE v.buyer.id =:userId")
    List<Voucher> getBuyersVouchers(@Param("userId") Long userId);

    @Query("SELECT v FROM Voucher v WHERE v.issuer.id =:issuerId")
    List<Voucher> getIssuersVouchers(@Param("issuerId") Long issuerId);
}
