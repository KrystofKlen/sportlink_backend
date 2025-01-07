package com.sportlink.sportlink.balance;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class H2_CurrencyRepository implements I_CurrencyRepository {

    private final JPA_Currency_Repository jpaRepository;

    public H2_CurrencyRepository(JPA_Currency_Repository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Currency saveCurrency(Currency currency) {
        return null;
    }

    @Override
    public Optional<Currency> findCurrencyByName(String currency) {
        return Optional.empty();
    }
}
