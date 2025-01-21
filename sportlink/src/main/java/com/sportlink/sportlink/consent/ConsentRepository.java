package com.sportlink.sportlink.consent;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsentRepository extends JpaRepository<Consent, Long> {
    List<Consent> getConsentsByAccountId(Long accountId);
}
