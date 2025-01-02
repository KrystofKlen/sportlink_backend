package com.sportlink.sportlink.balance;

import java.util.Optional;

public interface I_BalanceRepository {
    Balance saveBalance(Balance balance);
    Optional<Currency> findCurrencyByName(CURRENCY currency);
    Optional<Balance> findBalanceById(Long id);
}
