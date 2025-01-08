package com.sportlink.sportlink.verification.reward;

import com.sportlink.sportlink.claim.ClaimService;
import com.sportlink.sportlink.claim.DTO_Claim;
import com.sportlink.sportlink.reward.DTO_Reward;
import com.sportlink.sportlink.verification.I_VerificationStrategy;
import com.sportlink.sportlink.verification.reward.conditions.ClaimLimit;
import com.sportlink.sportlink.verification.reward.conditions.ClaimTimeRange;
import com.sportlink.sportlink.verification.reward.contexts.ComparisonContext;
import com.sportlink.sportlink.verification.reward.contexts.IntervalContext;
import com.sportlink.sportlink.visit.DTO_Visit;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RewardVerificationFactory {

    private final ClaimService claimService;

    public RewardVerificationFactory(ClaimService claimService) {
        this.claimService = claimService;
    }

    public List<I_VerificationStrategy> createConditionsList(DTO_Visit dtoVisit, long userId, DTO_Reward reward) {

        List<I_VerificationStrategy> list = new ArrayList<I_VerificationStrategy>();

        reward.getRewardConditions().forEach(condition -> {
            switch (condition) {
                case TOTAL_CLAIMS_LIMIT -> {
                    ComparisonContext comparisonContext = new ComparisonContext();
                    comparisonContext.setBoundary(reward.getTotalClaimsLimit());
                    int claims = claimService.getClaimsForReward(reward.getId()).size();
                    comparisonContext.setGiven(claims);
                    list.add(new ClaimLimit(comparisonContext));
                }
                case MONTHLY_CLAIMS_LIMIT -> {
                    ComparisonContext comparisonContext = new ComparisonContext();
                    comparisonContext.setBoundary(reward.getMonthClaimsLimit());
                    List<DTO_Claim> claimsTotal = claimService.getClaimsForReward(reward.getId());
                    int claims = claimService.getThisMonth(claimsTotal).size();
                    comparisonContext.setGiven(claims);
                    list.add(new ClaimLimit(comparisonContext));
                }
                case DAILY_USER__CLAIMS_LIMIT -> {
                    ComparisonContext comparisonContext = new ComparisonContext();
                    comparisonContext.setBoundary(reward.getDayLimitPerUser());
                    List<DTO_Claim> claims = claimService.getClaimsForReward(reward.getId());
                    claims = claimService.getThisMonth(claims);
                    claims = claimService.getUsersClaims(userId, claims);
                    comparisonContext.setGiven(claims.size());
                    list.add(new ClaimLimit(comparisonContext));
                }
                case CLAIM_TIME_RANGE -> {
                    IntervalContext intervalContext = new IntervalContext();
                    intervalContext.setTimestampStart(dtoVisit.getTimestampStart());
                    intervalContext.setTimestampStop(dtoVisit.getTimestampStop());
                    intervalContext.setIntervals(reward.getIntervals());
                    list.add(new ClaimTimeRange(intervalContext));
                }
                case MIN_TIME_SPENT -> {
                    LocalDateTime start = dtoVisit.getTimestampStart();
                    LocalDateTime stop = dtoVisit.getTimestampStop();
                    Duration duration = Duration.between(start, stop);
                    long minutes = duration.toMinutes();
                    ComparisonContext comparisonContext = new ComparisonContext();
                    comparisonContext.setBoundary((int) minutes);
                    comparisonContext.setGiven(reward.getMinMinutesSpent());
                    list.add(new ClaimLimit(comparisonContext));
                }
            }
        });

        return list;
    }
}
