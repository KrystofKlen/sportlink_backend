package com.sportlink.sportlink.balance;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class DTO_Balance {
    private Long id;
    private Map<DTO_Currency, Integer> amounts = new HashMap<>();
}

