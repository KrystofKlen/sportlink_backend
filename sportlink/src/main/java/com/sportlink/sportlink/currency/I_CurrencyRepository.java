package com.sportlink.sportlink.currency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface I_CurrencyRepository extends JpaRepository<Currency, Long> {
    @Query("SELECT c FROM Currency c WHERE c.name =:currency")
    Optional<Currency> findCurrencyByName(@Param("currency") String currency);
    @Query("SELECT c FROM Currency c WHERE c.issuer.id =:issuerId")
    Optional<Currency> findByIssuer(@Param("issuerId") Long issuerId);
}
