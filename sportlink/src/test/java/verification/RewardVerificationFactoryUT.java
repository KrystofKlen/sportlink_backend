package verification;

import com.sportlink.sportlink.reward.DTO_Reward;
import com.sportlink.sportlink.verification.I_VerificationStrategy;
import com.sportlink.sportlink.verification.reward.RewardVerificationFactory;
import com.sportlink.sportlink.visit.DTO_Visit;
import com.sportlink.sportlink.visit.VisitState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static com.sportlink.sportlink.verification.reward.REWARD_CONDITION.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
public class RewardVerificationFactoryUT {

    private DTO_Reward reward;
    private DTO_Visit dtoVisit;

    private RewardVerificationFactory rewardVerificationFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rewardVerificationFactory = new RewardVerificationFactory();

        // Initialize DTO_Reward and set up test data
        reward = new DTO_Reward();
        reward.setId(1L);
        reward.setCurrency("USD");
        reward.setAmount(100);
        reward.setTotalClaimsLimit(10);
        reward.setTotalClaimsCount(1);
        reward.setMonthClaimsLimit(5);
        reward.setMonthClaimsCount(1);
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
        // Execute
        List<I_VerificationStrategy> result = rewardVerificationFactory.createConditionsList(dtoVisit, reward);

        // Verify
        List<Boolean> actualResult = verifyStrategies(result);
        List<Boolean> expectedResult = List.of(true, true);

        assertEquals(expectedResult, actualResult, "Expected strategies don't match actual strategies");
    }

    @Test
    void testCreateConditionsList_withExceedingClaims() {
        // Execute
        reward.setTotalClaimsCount(11);
        List<I_VerificationStrategy> result = rewardVerificationFactory.createConditionsList(dtoVisit, reward);

        // Verify
        List<Boolean> actualResult = verifyStrategies(result);
        List<Boolean> expectedResult = List.of(false, true); // Exceeded claims (false) and short visit time (false)

        assertEquals(expectedResult, actualResult, "Expected strategies don't match actual strategies");
    }

    @Test
    void testCreateConditionsList_withInsufficientVisitTime() {
        reward.setMinMinutesSpent(1000);
        // Execute
        List<I_VerificationStrategy> result = rewardVerificationFactory.createConditionsList(dtoVisit, reward);

        // Verify
        List<Boolean> actualResult = verifyStrategies(result);
        List<Boolean> expectedResult = List.of(true, false); // Both conditions are true

        assertEquals(expectedResult, actualResult, "Expected strategies don't match actual strategies");
    }

    @Test
    void testAllConditions() {
        reward.setRewardConditions(List.of(TOTAL_CLAIMS_LIMIT,
                MONTHLY_CLAIMS_LIMIT,
                CLAIM_TIME_RANGE,
                MIN_TIME_SPENT));

        // Execute
        List<I_VerificationStrategy> result = rewardVerificationFactory.createConditionsList(dtoVisit, reward);

        // Verify
        List<Boolean> actualResult = verifyStrategies(result);
        List<Boolean> expectedResult = List.of(true, true, true, true);
        assertEquals(expectedResult, actualResult, "Expected strategies don't match actual strategies");
    }

    @Test
    void testAllConditions_shouldFail() {
        reward.setRewardConditions(List.of(
                TOTAL_CLAIMS_LIMIT,
                MONTHLY_CLAIMS_LIMIT,
                CLAIM_TIME_RANGE,
                MIN_TIME_SPENT));
        // Execute
        List<I_VerificationStrategy> result = rewardVerificationFactory.createConditionsList(dtoVisit, reward);

        // Verify
        List<Boolean> actualResult = verifyStrategies(result);
        List<Boolean> expectedResult = List.of(true, true, true, true);
        assertEquals(expectedResult, actualResult, "Expected strategies don't match actual strategies");

        // set
        reward.setTotalClaimsLimit(0);
        // Execute
        result = rewardVerificationFactory.createConditionsList(dtoVisit, reward);

        // Verify
        actualResult = verifyStrategies(result);
        expectedResult = List.of(false, true, true, true);
        assertEquals(expectedResult, actualResult, "Expected strategies don't match actual strategies");

        //set
        reward.setTotalClaimsLimit(3);
        reward.setTotalClaimsCount(3);

        reward.setMonthClaimsLimit(2);
        reward.setMonthClaimsCount(1);

        reward.setMinMinutesSpent(61);
        // Execute
        result = rewardVerificationFactory.createConditionsList(dtoVisit, reward);

        // Verify
        actualResult = verifyStrategies(result);
        expectedResult = List.of(false, true, true, false);
        assertEquals(expectedResult, actualResult, "Expected strategies don't match actual strategies");

    }
}
