package com.sportlink.sportlink.reward;

import com.sportlink.sportlink.currency.Currency;
import com.sportlink.sportlink.currency.I_CurrencyRepository;
import com.sportlink.sportlink.location.I_LocationRepository;
import com.sportlink.sportlink.location.Location;
import com.sportlink.sportlink.location.LocationService;
import com.sportlink.sportlink.utils.DTO_Adapter;
import com.sportlink.sportlink.verification.I_VerificationStrategy;
import com.sportlink.sportlink.verification.reward.RewardVerificationFactory;
import com.sportlink.sportlink.visit.DTO_Visit;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RewardService {

    private final I_RewardRepository rewardRepository;
    private final DTO_Adapter adapter;
    private final RewardVerificationFactory verificationFactory;
    private final I_CurrencyRepository currencyRepository;
    private final I_LocationRepository locationRepository;

    public RewardService(I_RewardRepository rewardRepository, DTO_Adapter adapter, RewardVerificationFactory verificationFactory, I_CurrencyRepository currencyRepository, LocationService locationService, I_LocationRepository locationRepository) {
        this.rewardRepository = rewardRepository;
        this.adapter = adapter;
        this.verificationFactory = verificationFactory;
        this.currencyRepository = currencyRepository;
        this.locationRepository = locationRepository;
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
        return adapter.getDTO_Reward(rewardRepository.save(reward));
    }

    // only those with not null value updated
    public DTO_Reward update( DTO_Reward dto ) {

        Reward existing = rewardRepository.getReward(dto.getId()).orElseThrow();

        if(dto.getAmount() != null){
            existing.setAmount(dto.getAmount());
        }
        if(dto.getRewardConditions() != null){
            existing.setRewardConditions(dto.getRewardConditions());
        }
        if(dto.getTotalClaimsLimit() != null){
            existing.setMonthClaimsLimit(dto.getMonthClaimsLimit());
        }
        if(dto.getMonthClaimsLimit() != null){
            existing.setMonthClaimsLimit(dto.getMonthClaimsLimit());
        }
        if(dto.getIntervals() != null){
            existing.setIntervals(dto.getIntervals());
        }
        if(dto.getMinMinutesSpent() != null){
            existing.setMinMinuteSpend(dto.getMinMinutesSpent());
        }

        return adapter.getDTO_Reward(rewardRepository.save(existing));
    }

    public List<DTO_Reward> getRewardsForLocation(Long locationId) {
        Location location = locationRepository.findById(locationId).orElseThrow();
        return location.getRewards()
                .stream().map(adapter::getDTO_Reward).collect(Collectors.toList());
    }

    public Optional<Reward> getRewardById(Long id) {
        return rewardRepository.getReward(id);
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

    public List<DTO_Reward> addNewRewardForLocation( @Valid  DTO_Reward dto, long locationId){
        Location location = locationRepository.findById(locationId).orElseThrow();
        // Check if the LocationReward already exists for the given location ID
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
        reward.setRewardConditions(dto.getRewardConditions());

        location.getRewards().add(reward);
        locationRepository.save(location);
        return location.getRewards().stream().map(adapter::getDTO_Reward).collect(Collectors.toList());
    }
}
