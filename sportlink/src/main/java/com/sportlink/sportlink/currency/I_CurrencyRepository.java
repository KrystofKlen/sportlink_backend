package com.sportlink.sportlink.balance;

import java.util.Optional;

public interface I_CurrencyRepository {
    Currency saveCurrency(Currency currency);
    Optional<Currency> findCurrencyByName(String currency);
}
