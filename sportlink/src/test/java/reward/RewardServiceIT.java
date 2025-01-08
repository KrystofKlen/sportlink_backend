package reward;

import com.sportlink.sportlink.SportlinkApplication;
import com.sportlink.sportlink.account.Account;
import com.sportlink.sportlink.currency.Currency;
import com.sportlink.sportlink.currency.I_CurrencyRepository;
import com.sportlink.sportlink.location.Location;
import com.sportlink.sportlink.reward.*;
import com.sportlink.sportlink.utils.DTO_Adapter;
import com.sportlink.sportlink.visit.DTO_Visit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
        currencyRepository.saveCurrency(currency);

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
        Optional<Reward> foundReward = rewardRepository.getReward(savedReward.getId());

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
        Optional<Reward> foundReward = rewardRepository.getReward(updatedReward.getId());

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

}
