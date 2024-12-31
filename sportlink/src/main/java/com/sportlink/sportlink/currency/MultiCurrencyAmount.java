package com.sportlink.sportlink.currency;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Entity
@Data
public class MultiCurrencyAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ElementCollection
    @CollectionTable(
            name = "currency_amounts",
            joinColumns = @JoinColumn(name = "multi_currency_amount_id")
    )
    @MapKeyJoinColumn(name = "currency_id")  // Link to Currency entity
    @Column(name = "amount")  // Store the amount as the column value
    private Map<Currency, Integer> amounts = new HashMap<>();

}
