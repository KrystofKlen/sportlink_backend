package com.sportlink.sportlink.reward;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Primary
@Repository
public class H2_RewardRepository implements I_RewardRepository {

    private final JPA_RewardRepository jpaRewardRepository;

    public H2_RewardRepository(JPA_RewardRepository jpaRewardRepository) {
        this.jpaRewardRepository = jpaRewardRepository;
    }

    @Override
    public Optional<Reward> getReward(Long id) {

        return jpaRewardRepository.findById(id);
    }

    @Override
    public Reward save(Reward reward) {
        return jpaRewardRepository.save(reward);
    }

}
