package com.sportlink.sportlink.codes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JPA_CodesRepository extends JpaRepository<CodeData, Long> {

    @Query("SELECT q FROM CodeData q WHERE q.code = :qrCode")
    Optional<CodeData> findByQrCode(@Param("qrCode") String qrCode);
}
