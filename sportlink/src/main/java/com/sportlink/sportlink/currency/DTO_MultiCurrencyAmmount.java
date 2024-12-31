package com.sportlink.sportlink.currency;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class DTO_MultiCurrencyAmmount {
    private Map<DTO_Currency, Integer> amounts = new HashMap<>();
}
