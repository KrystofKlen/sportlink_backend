package com.sportlink.sportlink.reward;

import com.sportlink.sportlink.currency.Currency;
import com.sportlink.sportlink.currency.I_CurrencyRepository;
import com.sportlink.sportlink.utils.DTO_Adapter;
import com.sportlink.sportlink.verification.I_VerificationStrategy;
import com.sportlink.sportlink.verification.reward.RewardVerificationFactory;
import com.sportlink.sportlink.visit.DTO_Visit;
import com.sportlink.sportlink.visit.Visit;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RewardService {

    private final I_RewardRepository rewardRepository;
    private final DTO_Adapter adapter;
    private final RewardVerificationFactory verificationFactory;
    private final I_CurrencyRepository currencyRepository;

    public RewardService(I_RewardRepository rewardRepository, DTO_Adapter adapter, RewardVerificationFactory verificationFactory, I_CurrencyRepository currencyRepository) {
        this.rewardRepository = rewardRepository;
        this.adapter = adapter;
        this.verificationFactory = verificationFactory;
        this.currencyRepository = currencyRepository;
    }

    public DTO_Reward save(@Valid DTO_Reward dto) {

        Currency currency = currencyRepository.findCurrencyByName(dto.getCurrency()).orElseThrow();

        Reward reward = new Reward(
                null,
                dto.getRewardConditions(),
                currency,
                dto.getAmount(),
                dto.getTotalClaimsLimit(),
                dto.getTotalClaimsCount(),
                dto.getMonthClaimsLimit(),
                dto.getMonthClaimsCount(),
                dto.getIntervals(),
                dto.getMinMinutesSpent()
        );

        Reward saved = rewardRepository.save(reward);
        DTO_Reward dtoSaved = adapter.getDTO_Reward(saved);

        log.info("Reward saved: " + dtoSaved);

        return dtoSaved;
    }

    // only those with not null value updated
    public DTO_Reward update(DTO_Reward dto) {

        Reward existing = rewardRepository.findById(dto.getId()).orElseThrow();

        if (dto.getAmount() != null) {
            existing.setAmount(dto.getAmount());
        }
        if (dto.getRewardConditions() != null) {
            existing.setRewardConditions(dto.getRewardConditions());
        }
        if (dto.getTotalClaimsLimit() != null) {
            existing.setMonthClaimsLimit(dto.getMonthClaimsLimit());
        }
        if (dto.getMonthClaimsLimit() != null) {
            existing.setMonthClaimsLimit(dto.getMonthClaimsLimit());
        }
        if (dto.getIntervals() != null) {
            existing.setIntervals(dto.getIntervals());
        }
        if (dto.getMinMinutesSpent() != null) {
            existing.setMinMinuteSpend(dto.getMinMinutesSpent());
        }

        Reward saved = rewardRepository.save(existing);
        DTO_Reward dtoSaved = adapter.getDTO_Reward(saved);

        log.info("Reward updated: Id = " + existing.getId());

        return adapter.getDTO_Reward(rewardRepository.save(existing));
    }

    public Optional<Reward> getRewardById(Long id) {
        return rewardRepository.findById(id);
    }

    public boolean verifyConditions(DTO_Visit visit, DTO_Reward claimedReward) {
        List<I_VerificationStrategy> conditions = verificationFactory.createConditionsList(visit, claimedReward);
        for (I_VerificationStrategy condition : conditions) {
            if (!condition.verify()) {
                return false;
            }
        }
        return true;
    }

    public List<Reward> getApprovedRewards(List<Reward> rewards, Visit visit) {
        DTO_Visit dtoVisit = adapter.getDTO_Visit(visit);
        List<Reward> approvedRewards = new ArrayList<>();
        rewards.forEach(r -> {
            DTO_Reward dto = adapter.getDTO_Reward(r);
            boolean verified = verifyConditions(dtoVisit, dto);
            if (verified) {
                r.setTotalClaimsCount(dto.getTotalClaimsCount() + 1);
                r.setMonthClaimsCount(dto.getMonthClaimsCount() + 1);
                approvedRewards.add(r);
            }
        });
        return approvedRewards;
    }

    @Scheduled(cron = "0 0 0 1 * ?") // Runs at midnight on the 1st day of every month
    public void resetMonthlyClaims() {
        List<Reward> rewards = rewardRepository.findAll();
        for (Reward reward : rewards) {
            reward.setMonthClaimsCount(0); // Reset the count
        }
        rewardRepository.saveAll(rewards);
    }
}
