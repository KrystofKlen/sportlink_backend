package com.sportlink.sportlink.reward;

import java.util.Optional;

public interface I_RewardRepository {
    Optional<Reward> getReward(Long id);

    Reward save(Reward reward);
}
