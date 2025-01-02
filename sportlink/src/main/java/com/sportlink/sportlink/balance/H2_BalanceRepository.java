package com.sportlink.sportlink.balance;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class H2_BalanceRepository implements I_BalanceRepository {

    private final JPA_Balance_Repository jpaRepository;

    public H2_BalanceRepository(JPA_Balance_Repository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Balance saveBalance(Balance balance) {
        return jpaRepository.save(balance);
    }

    @Override
    public Optional<Currency> findCurrencyByName(CURRENCY currency) {
        return jpaRepository.findByName(currency);
    }

    @Override
    public Optional<Balance> findBalanceById(Long id) {
        return Optional.empty();
    }
}
