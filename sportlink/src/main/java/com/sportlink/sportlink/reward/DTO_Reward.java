package com.sportlink.sportlink.reward;

import com.sportlink.sportlink.utils.TimeInterval;
import com.sportlink.sportlink.verification.reward.REWARD_CONDITION;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DTO_Reward {
    private Long id;

    @NotNull
    private String currency;

    @NotNull
    @Size(min = 1)
    private Integer amount;

    @NotNull
    @NotEmpty
    private List<REWARD_CONDITION> rewardConditions;

    @NotNull
    private Integer totalClaimsLimit;
    @NotNull
    private Integer totalClaimsCount;

    @NotNull
    private Integer monthClaimsLimit;
    @NotNull
    private Integer monthClaimsCount;

    @NotNull
    private List<TimeInterval> intervals;

    @NotNull
    private Integer minMinutesSpent;

}
