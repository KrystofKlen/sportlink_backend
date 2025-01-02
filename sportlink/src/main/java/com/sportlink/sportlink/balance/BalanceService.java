package com.sportlink.sportlink.balance;

import com.sportlink.sportlink.utils.DTO_Adapter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class BalanceService {

    private final I_BalanceRepository balanceRepository;
    private final DTO_Adapter dto_adapter;

    public BalanceService(I_BalanceRepository balanceRepository, DTO_Adapter dto_adapter) {
        this.balanceRepository = balanceRepository;
        this.dto_adapter = dto_adapter;
    }

    // Adds b to a
    public DTO_Balance adjust(DTO_Balance a, DTO_Balance b, int sign) {

        Map<DTO_Currency, Integer> clearA = new HashMap<>();
        Map<DTO_Currency, Integer> clearB = new HashMap<>();

        a.getAmounts().forEach((dtoCurrency, integer) -> {
            if(integer != 0){
                clearA.put(dtoCurrency, integer);
            }
        });
        b.getAmounts().forEach((dtoCurrency, integer) -> {
            if(integer != 0){
                clearB.put(dtoCurrency, integer);
            }
        });

        // Create new map
        Map<DTO_Currency, Integer> result = new HashMap<>();

        result.putAll(clearA);

        clearB.forEach((currB, amountB) -> {
            if(result.containsKey(currB)) {
                int newAmount = result.get(currB) + sign*amountB;
                if(newAmount == 0){
                    result.remove(currB);
                }else{
                    result.put(currB, newAmount);
                }
            } else {
                result.put(currB, amountB);
            }
        });

        return new DTO_Balance(a.getId(), result);
    }

    // Returns true if all currencies have positive, or zero balance
    public boolean hasPositiveBalance(DTO_Balance multiCurrencyAmount) {
        return multiCurrencyAmount.getAmounts().values().stream().allMatch(amount -> amount >= 0);
    }

    public Optional<DTO_Currency> getCurrency(CURRENCY name) {
        Optional<Currency> currency = balanceRepository.findCurrencyByName(name);
        return currency.map(dto_adapter::getDTO_Currency);
    }

    // If creating new multiCurrency - pass null as inventoryAccountId
    @Transactional
    public Balance save(DTO_Balance dto, Long existingId) {

        // create new amounts
        Map<Currency, Integer> entityAmounts = new HashMap<>();

        // create entity Currencies from dtos
        dto.getAmounts().forEach((dtoCurrency, amount) -> {
            Optional<Currency> optionalCurrency = balanceRepository.findCurrencyByName(dtoCurrency.getCurrency());
            if (optionalCurrency.isEmpty()) {
                throw new EntityNotFoundException("Currency with given name does not exist.");
            }
            entityAmounts.put(optionalCurrency.get(), amount);
        });

        // find if account already has multiCurrency
        Optional<Balance> existing = balanceRepository.findBalanceById(existingId);
        if (existing.isPresent()) {
            existing.get().setAmounts(entityAmounts);
            // save (update) existing multiCurrency
            return balanceRepository.saveBalance(existing.get());
        }

        // save (create new) multiCurrency
        Balance balance = new Balance(null, entityAmounts);
        return balanceRepository.saveBalance(balance);
    }

}
