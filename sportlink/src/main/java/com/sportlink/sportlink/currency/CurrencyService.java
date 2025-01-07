package com.sportlink.sportlink.balance;

import com.sportlink.sportlink.utils.DTO_Adapter;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CurrencyService {

    private final I_CurrencyRepository balanceRepository;
    private final DTO_Adapter dto_adapter;

    public CurrencyService(I_CurrencyRepository balanceRepository, DTO_Adapter dto_adapter) {
        this.balanceRepository = balanceRepository;
        this.dto_adapter = dto_adapter;
    }

    public Optional<Currency> getCurrency(String currency) {
        return balanceRepository.findCurrencyByName(currency);
    }

}
