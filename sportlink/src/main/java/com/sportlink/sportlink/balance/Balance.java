package com.sportlink.sportlink.balance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Balance {

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
