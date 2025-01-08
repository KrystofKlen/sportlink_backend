package verification;

import com.sportlink.sportlink.claim.ClaimService;
import com.sportlink.sportlink.claim.DTO_Claim;
import com.sportlink.sportlink.reward.DTO_Reward;
import com.sportlink.sportlink.verification.I_VerificationStrategy;
import com.sportlink.sportlink.verification.reward.RewardVerificationFactory;
import com.sportlink.sportlink.visit.DTO_Visit;
import com.sportlink.sportlink.visit.VisitState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static com.sportlink.sportlink.verification.reward.REWARD_CONDITION.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class RewardVerificationFactoryUT {

    @Mock
    private ClaimService claimService;

    private DTO_Reward reward;
    private DTO_Visit dtoVisit;

    private RewardVerificationFactory rewardVerificationFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rewardVerificationFactory = new RewardVerificationFactory(claimService);

        // Initialize DTO_Reward and set up test data
        reward = new DTO_Reward();
        reward.setId(1L);
        reward.setCurrency("USD");
        reward.setAmount(100);
        reward.setTotalClaimsLimit(10);
        reward.setMonthClaimsLimit(5);
        reward.setDayLimitPerUser(2);
        reward.setMinMinutesSpent(30);
        reward.setRewardConditions(List.of(
                TOTAL_CLAIMS_LIMIT,
                MIN_TIME_SPENT)
        );
        reward.setIntervals(List.of());

        // Initialize DTO_Visit and set up test data
        dtoVisit = new DTO_Visit();
        dtoVisit.setVisitId(1L);
        dtoVisit.setVisitState(VisitState.OPEN);
        dtoVisit.setTimestampStart(LocalDateTime.now().minusSeconds(3600)); // 1 hour ago
        dtoVisit.setTimestampStop(LocalDateTime.now()); // Now
    }

    private List<Boolean> verifyStrategies(List<I_VerificationStrategy> strategies) {
        List<Boolean> results = new java.util.ArrayList<>();
        for (I_VerificationStrategy strategy : strategies) {
            results.add(strategy.verify());
        }
        return results;
    }

    @Test
    void testCreateConditionsList_withValidData() {
        // Setup: Claim limit 10, current visit time 60 minutes
        // Reward conditions: TOTAL_CLAIMS_LIMIT and MIN_TIME_SPENT
        // Mock services to return appropriate data
        when(claimService.getClaimsForReward(reward.getId())).thenReturn(List.of(new DTO_Claim(), new DTO_Claim())); // Mock 2 claims for reward

        // Execute
        List<I_VerificationStrategy> result = rewardVerificationFactory.createConditionsList(dtoVisit, 1L, reward);

        // Verify
        List<Boolean> actualResult = verifyStrategies(result);
        List<Boolean> expectedResult = List.of(true, true);

        assertEquals(expectedResult, actualResult, "Expected strategies don't match actual strategies");
    }

    @Test
    void testCreateConditionsList_withExceedingClaims() {
        // Setup: Exceed claims limit 12/10, visit time 20 minutes
        // Reward conditions: TOTAL_CLAIMS_LIMIT and MIN_TIME_SPENT
        // Mock services to return excessive claims and short visit time
        when(claimService.getClaimsForReward(reward.getId())).thenReturn(List.of(new DTO_Claim(), new DTO_Claim(), new DTO_Claim(), new DTO_Claim(), new DTO_Claim(), new DTO_Claim(), new DTO_Claim(), new DTO_Claim(), new DTO_Claim(), new DTO_Claim(), new DTO_Claim(), new DTO_Claim())); // Mock 12 claims for reward

        // Execute
        List<I_VerificationStrategy> result = rewardVerificationFactory.createConditionsList(dtoVisit, 1L, reward);

        // Verify
        List<Boolean> actualResult = verifyStrategies(result);
        List<Boolean> expectedResult = List.of(false, true); // Exceeded claims (false) and short visit time (false)

        assertEquals(expectedResult, actualResult, "Expected strategies don't match actual strategies");
    }

    @Test
    void testCreateConditionsList_withInsufficientVisitTime() {
        // Setup: Valid visit time of 60 minutes, within minimum limit
        // Reward conditions: MIN_TIME_SPENT
        // Mock services to return valid visit time
        when(claimService.getClaimsForReward(reward.getId())).thenReturn(List.of(new DTO_Claim(), new DTO_Claim())); // Mock 2 claims for reward
        reward.setMinMinutesSpent(1000);
        // Execute
        List<I_VerificationStrategy> result = rewardVerificationFactory.createConditionsList(dtoVisit, 1L, reward);

        // Verify
        List<Boolean> actualResult = verifyStrategies(result);
        List<Boolean> expectedResult = List.of(true, false); // Both conditions are true

        assertEquals(expectedResult, actualResult, "Expected strategies don't match actual strategies");
    }

    @Test
    void testAllConditions() {
        reward.setRewardConditions(List.of(TOTAL_CLAIMS_LIMIT,
                MONTHLY_CLAIMS_LIMIT,
                DAILY_USER__CLAIMS_LIMIT,
                CLAIM_TIME_RANGE,
                MIN_TIME_SPENT));
        when(claimService.getClaimsForReward(reward.getId())).thenReturn(List.of(new DTO_Claim(), new DTO_Claim()));

        // Execute
        List<I_VerificationStrategy> result = rewardVerificationFactory.createConditionsList(dtoVisit, 1L, reward);

        // Verify
        List<Boolean> actualResult = verifyStrategies(result);
        List<Boolean> expectedResult = List.of(true, true, true, true, true);
        assertEquals(expectedResult, actualResult, "Expected strategies don't match actual strategies");
    }

    @Test
    void testAllConditions_shouldFail() {
        reward.setRewardConditions(List.of(
                TOTAL_CLAIMS_LIMIT,
                MONTHLY_CLAIMS_LIMIT,
                DAILY_USER__CLAIMS_LIMIT,
                CLAIM_TIME_RANGE,
                MIN_TIME_SPENT));
        when(claimService.getClaimsForReward(reward.getId())).thenReturn(List.of(new DTO_Claim(), new DTO_Claim()));
        reward.setDayLimitPerUser(0);
        // Execute
        List<I_VerificationStrategy> result = rewardVerificationFactory.createConditionsList(dtoVisit, 1L, reward);

        // Verify
        List<Boolean> actualResult = verifyStrategies(result);
        List<Boolean> expectedResult = List.of(true, true, false, true, true);
        assertEquals(expectedResult, actualResult, "Expected strategies don't match actual strategies");

        // set
        reward.setTotalClaimsLimit(0);
        reward.setDayLimitPerUser(1);
        // Execute
        result = rewardVerificationFactory.createConditionsList(dtoVisit, 1L, reward);

        // Verify
        actualResult = verifyStrategies(result);
        expectedResult = List.of(false, true, true, true, true);
        assertEquals(expectedResult, actualResult, "Expected strategies don't match actual strategies");

        //set
        when(claimService.getThisMonth(any())).thenReturn(List.of(new DTO_Claim(), new DTO_Claim()));
        when(claimService.getUsersClaims(any(), any())).thenReturn(List.of(new DTO_Claim(), new DTO_Claim()));
        reward.setTotalClaimsLimit(3);
        reward.setMonthClaimsLimit(2);
        reward.setDayLimitPerUser(2);
        reward.setMinMinutesSpent(61);
        // Execute
        result = rewardVerificationFactory.createConditionsList(dtoVisit, 1L, reward);

        // Verify
        actualResult = verifyStrategies(result);
        expectedResult = List.of(true, false, false, true, false);
        assertEquals(expectedResult, actualResult, "Expected strategies don't match actual strategies");

    }
}
