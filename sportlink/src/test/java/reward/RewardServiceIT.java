package reward;

import com.sportlink.sportlink.SportlinkApplication;
import com.sportlink.sportlink.account.account.Account;
import com.sportlink.sportlink.currency.Currency;
import com.sportlink.sportlink.currency.I_CurrencyRepository;
import com.sportlink.sportlink.reward.DTO_Reward;
import com.sportlink.sportlink.reward.I_RewardRepository;
import com.sportlink.sportlink.reward.Reward;
import com.sportlink.sportlink.reward.RewardService;
import com.sportlink.sportlink.utils.DTO_Adapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(classes = SportlinkApplication.class)
public class RewardServiceIT {

    @Autowired
    private RewardService rewardService;

    @Autowired
    private I_RewardRepository rewardRepository;

    @Autowired
    private I_CurrencyRepository currencyRepository;

    @Autowired
    private DTO_Adapter adapter;

    private DTO_Reward reward;

    @BeforeEach
    void setUp() {
        Account account = new Account();
        Currency currency = new Currency();
        currency.setName("ABC");
        currency.setIssuer(account);
        currencyRepository.save(currency);

        reward = new DTO_Reward();
        reward.setAmount(30);
        reward.setMinMinutesSpent(10);
        reward.setCurrency("ABC");
        reward.setTotalClaimsLimit(100);
        reward.setTotalClaimsCount(1);
        reward.setMonthClaimsLimit(20);
        reward.setMonthClaimsCount(1);
        reward.setIntervals(List.of());
        reward.setMinMinutesSpent(30);
    }

    @Test
    void testSaveReward() {

        // Save the reward
        DTO_Reward savedReward = rewardService.save(reward);

        // Fetch the reward from the database
        Optional<Reward> foundReward = rewardRepository.findById(savedReward.getId());

        // Assert that it was saved correctly
        assertTrue(foundReward.isPresent());
        assertEquals(reward.getAmount(), foundReward.get().getAmount());
        assertEquals(reward.getMinMinutesSpent(), foundReward.get().getMinMinuteSpend());
    }

    @Test
    void testUpdateReward() {
        // Save the reward first
        DTO_Reward savedReward = rewardService.save(reward);

        // Update the reward details
        savedReward.setAmount(500);
        savedReward.setMinMinutesSpent(300);
        DTO_Reward updatedReward = rewardService.update(savedReward);

        // Fetch the updated reward from the database
        Optional<Reward> foundReward = rewardRepository.findById(updatedReward.getId());

        // Assert that the reward was updated correctly
        assertTrue(foundReward.isPresent());
        assertEquals(500, foundReward.get().getAmount());
        assertEquals(300, foundReward.get().getMinMinuteSpend());
    }

    @Test
    void testGetRewardById() {
        // Save a reward
        DTO_Reward savedReward = rewardService.save(reward);

        // Fetch the reward by its ID
        Optional<Reward> foundReward = rewardService.getRewardById(savedReward.getId());

        // Assert the reward is found and matches the original reward
        assertTrue(foundReward.isPresent());
        assertEquals(savedReward.getId(), foundReward.get().getId());
        assertEquals(savedReward.getAmount(), foundReward.get().getAmount());
    }

    @Test
    void testMonthlyClaimReset() {
        // Save a reward
        DTO_Reward savedReward = rewardService.save(reward);

        // Manually increment monthClaimsCount
        Optional<Reward> reward = rewardRepository.findById(savedReward.getId());
        assertThat(reward).isPresent();
        Reward foundReward = reward.get();
        foundReward.setMonthClaimsCount(10);
        rewardRepository.save(foundReward);

        // Verify the count before reset
        assertThat(foundReward.getMonthClaimsCount()).isEqualTo(10);

        // Simulate the reset logic (e.g., call a method in your service)
        rewardService.resetMonthlyClaims();

        // Fetch the updated reward
        Reward resetReward = rewardRepository.findById(savedReward.getId()).orElseThrow();
        assertThat(resetReward.getMonthClaimsCount()).isEqualTo(0);
    }

}
