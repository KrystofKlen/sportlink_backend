package com.sportlink.sportlink.consent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConsentRepository extends JpaRepository<Consent, Long> {

    @Query("SELECT c FROM Consent c WHERE c.account.id =:accountId")
    List<Consent> getConsentsByAccountId(@Param("accountId") Long accountId);
}
