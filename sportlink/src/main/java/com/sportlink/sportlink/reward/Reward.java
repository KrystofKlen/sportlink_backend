package com.sportlink.sportlink.reward;

import com.sportlink.sportlink.currency.Currency;
import com.sportlink.sportlink.utils.TimeInterval;
import com.sportlink.sportlink.verification.reward.REWARD_CONDITION;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    private List<REWARD_CONDITION> rewardConditions;

    @ManyToOne
    private Currency currency;

    private int amount;

    private int totalClaimsLimit;
    private int totalClaimsCount;

    private int monthClaimsLimit;
    private int monthClaimsCount;

    @ElementCollection
    private List<TimeInterval> intervals;

    private int minMinuteSpend;

}
