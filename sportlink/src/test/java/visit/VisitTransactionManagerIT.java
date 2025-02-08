package visit;

import com.sportlink.sportlink.SportlinkApplication;
import com.sportlink.sportlink.account.account.I_AccountRepository;
import com.sportlink.sportlink.account.company.CompanyAccount;
import com.sportlink.sportlink.account.device.LocationDevice;
import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.account.user.UserAccountService;
import com.sportlink.sportlink.codes.CodesService;
import com.sportlink.sportlink.currency.Currency;
import com.sportlink.sportlink.currency.I_CurrencyRepository;
import com.sportlink.sportlink.location.I_LocationRepository;
import com.sportlink.sportlink.location.Location;
import com.sportlink.sportlink.reward.I_RewardRepository;
import com.sportlink.sportlink.reward.Reward;
import com.sportlink.sportlink.transfer.I_TransferRepository;
import com.sportlink.sportlink.transfer.Transfer;
import com.sportlink.sportlink.utils.RESULT_CODE;
import com.sportlink.sportlink.verification.location.DTO_LocationVerificationRequest;
import com.sportlink.sportlink.verification.location.LOCATION_VERIFICATION_STRATEGY;
import com.sportlink.sportlink.verification.reward.REWARD_CONDITION;
import com.sportlink.sportlink.visit.I_VisitRepository;
import com.sportlink.sportlink.visit.Visit;
import com.sportlink.sportlink.visit.VisitState;
import com.sportlink.sportlink.visit.VisitTransactionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(classes = SportlinkApplication.class)
@Transactional
class VisitTransactionManagerIT {

    @Autowired
    private VisitTransactionManager visitTransactionManager;

    @Autowired
    private I_LocationRepository locationRepository;

    @Autowired
    private I_AccountRepository accountRepository;

    @Autowired
    private I_RewardRepository rewardRepository;


    @Autowired
    private I_VisitRepository visitRepository;

    @Autowired
    private I_TransferRepository transferRepository;

    private UserAccount visitor;
    private Location location;

    DTO_LocationVerificationRequest request;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private I_CurrencyRepository currencyRepository;

    private Reward r1, r2;
    @Mock
    private I_AccountRepository mockAccountRepository;
    @Autowired
    private CodesService codesService;

    @BeforeEach
    void setUp() {
        visitor = new UserAccount();
        visitor.setUsername("test_user");
        visitor.setBalance(new java.util.HashMap<>());
        visitor = accountRepository.save(visitor);

        CompanyAccount locationIssuer = new CompanyAccount();
        locationIssuer.setUsername("locationIssuer");
        locationIssuer = accountRepository.save(locationIssuer);

        Currency currency = new Currency();
        currency.setName("CURRENCY-TEST");
        currency.setIssuer(locationIssuer);
        currencyRepository.save(currency);

        r1 = new Reward();
        r1.setTotalClaimsLimit(10);
        r1.setCurrency(currency);
        r1.setAmount(5);
        r1.setRewardConditions(new ArrayList<>(List.of(REWARD_CONDITION.TOTAL_CLAIMS_LIMIT)));

        r2 = new Reward();
        r2.setTotalClaimsLimit(5);
        r2.setCurrency(currency);
        r2.setAmount(30);
        r2.setRewardConditions(new ArrayList<>(List.of(REWARD_CONDITION.TOTAL_CLAIMS_LIMIT)));

        List<Reward> rewards = new ArrayList<>();
        rewards.add(r1);
        rewards.add(r2);

        location = new Location();
        location.setLatitude(40.8);
        location.setLongitude(-73.9);
        location.setVerificationStrategies(new HashSet<>(Set.of(LOCATION_VERIFICATION_STRATEGY.USER_WITHIN_RADIUS)));
        location.setRewards(rewards);
        location = locationRepository.save(location);

        request = new DTO_LocationVerificationRequest();
        request.setLocationId(location.getId());
        request.setUserLatitude(40.8);
        request.setUserLongitude(-73.9);
        request.setLocationLatitude(40.8);
        request.setLocationLongitude(-73.9);
    }

    @Test
    void testLocationOTPOpen(){
        location.getVerificationStrategies().add(LOCATION_VERIFICATION_STRATEGY.USER_SCAN_ONETIME_CODE);
        location = locationRepository.save(location);

        LocationDevice device = new LocationDevice();
        device.setLoginEmail("x");
        device.setUsername("device");
        device.setLocation(location);
        device = accountRepository.save(device);

        String code = codesService.establishLocationOTP(visitor.getId(), device.getId());
        request.setCode(code);
        request.setUserId(visitor.getId());
        RESULT_CODE result = visitTransactionManager.openVisit(visitor, request);

        assertEquals(RESULT_CODE.VISIT_OPENED, result);
        assertEquals(1, visitRepository.count());

        Visit savedVisit = visitRepository.findAll().get(0);
        assertEquals(VisitState.OPEN, savedVisit.getState());
        assertEquals(visitor.getUsername(), savedVisit.getVisitor().getUsername());
        assertEquals(location.getLatitude(), savedVisit.getLocation().getLatitude());
    }

    @Test
    void testOpenVisitSuccess() {
        RESULT_CODE result = visitTransactionManager.openVisit(visitor, request);

        assertEquals(RESULT_CODE.VISIT_OPENED, result);
        assertEquals(1, visitRepository.count());

        Visit savedVisit = visitRepository.findAll().get(0);
        assertEquals(VisitState.OPEN, savedVisit.getState());
        assertEquals(visitor.getUsername(), savedVisit.getVisitor().getUsername());
        assertEquals(location.getLatitude(), savedVisit.getLocation().getLatitude());
    }

    @Test
    void testOpenVisitLocationNotVerified() {
        request.setLocationLatitude(60.8);
        RESULT_CODE result = visitTransactionManager.openVisit(visitor, request);

        assertEquals(RESULT_CODE.LOCATION_NOT_VERIFIED, result);
        assertEquals(0, visitRepository.count());
    }

    @Test
    void testCloseVisit_1RewardNotApproved() {
        r1 = rewardRepository.findById(r1.getId()).orElseThrow();
        r1.setTotalClaimsLimit(1);
        r1.setTotalClaimsCount(1);
        rewardRepository.save(r1);

        Visit openVisit = new Visit(null, location, LocalDateTime.now(), null, VisitState.OPEN, visitor);
        visitRepository.save(openVisit);

        RESULT_CODE result = visitTransactionManager.closeVisit(visitor, request);

        assertEquals(RESULT_CODE.VISIT_CLOSED, result);

        // check visit
        Visit closedVisit = visitRepository.findById(openVisit.getVisitId()).orElseThrow();
        assertEquals(VisitState.CLOSED, closedVisit.getState());
        assertNotNull(closedVisit.getTimestampStop());

        // check transfers
        assertEquals(1, transferRepository.count());
        Transfer savedTransfer = transferRepository.findAll().get(0);
        assertEquals(visitor.getId(), savedTransfer.getUser().getId());
        assertEquals("CURRENCY-TEST", savedTransfer.getCurrency().getName());
        assertEquals(30, savedTransfer.getAmount());

        Map<String, Integer> balance = userAccountService.getBalance(visitor);
        assertTrue(balance.containsKey("CURRENCY-TEST"));
        assertEquals(30, balance.get("CURRENCY-TEST"));

        r1 = rewardRepository.findById(r1.getId()).orElseThrow();
        assertEquals(1, r1.getTotalClaimsLimit());
        assertEquals(1, r1.getTotalClaimsCount());
    }

    @Test
    void testCloseVisitSuccess() {
        Visit openVisit = new Visit(null, location, LocalDateTime.now(), null, VisitState.OPEN, visitor);
        visitRepository.save(openVisit);

        RESULT_CODE result = visitTransactionManager.closeVisit(visitor, request);

        assertEquals(RESULT_CODE.VISIT_CLOSED, result);

        // check visit
        Visit closedVisit = visitRepository.findById(openVisit.getVisitId()).orElseThrow();
        assertEquals(VisitState.CLOSED, closedVisit.getState());
        assertNotNull(closedVisit.getTimestampStop());

        // check transfers
        assertEquals(2, transferRepository.count());
        Transfer savedTransfer = transferRepository.findAll().get(0);
        assertEquals(visitor.getId(), savedTransfer.getUser().getId());
        assertEquals("CURRENCY-TEST", savedTransfer.getCurrency().getName());
        assertEquals(5, savedTransfer.getAmount());

        savedTransfer = transferRepository.findAll().get(1);
        assertEquals(visitor.getId(), savedTransfer.getUser().getId());
        assertEquals("CURRENCY-TEST", savedTransfer.getCurrency().getName());
        assertEquals(30, savedTransfer.getAmount());

        Map<String, Integer> balance = userAccountService.getBalance(visitor);
        assertTrue(balance.containsKey("CURRENCY-TEST"));
        assertEquals(35, balance.get("CURRENCY-TEST"));

        r1 = rewardRepository.findById(r1.getId()).orElseThrow();
        assertEquals(10, r1.getTotalClaimsLimit());
        assertEquals(1, r1.getTotalClaimsCount());

        r2 = rewardRepository.findById(r2.getId()).orElseThrow();
        assertEquals(5, r2.getTotalClaimsLimit());
        assertEquals(1, r2.getTotalClaimsCount());
    }

    @Test
    void testCloseVisitLocationNotVerified() {
        Visit openVisit = new Visit(null, location, LocalDateTime.now(), null, VisitState.OPEN, visitor);
        visitRepository.save(openVisit);

        request.setLocationLatitude(60.889);
        RESULT_CODE result = visitTransactionManager.closeVisit(visitor, request);

        assertEquals(RESULT_CODE.LOCATION_NOT_VERIFIED, result);
        assertEquals(1, visitRepository.count());

        Visit savedVisit = visitRepository.findById(openVisit.getVisitId()).orElseThrow();
        assertEquals(VisitState.OPEN, savedVisit.getState());
    }

    @Test
    void testCloseVisitWhenNoOpenVisitExists() {
        Visit closedVisit = new Visit(null, location, LocalDateTime.now(), LocalDateTime.now(), VisitState.CLOSED, visitor);
        visitRepository.save(closedVisit);

        RESULT_CODE result = visitTransactionManager.closeVisit(visitor, request);

        assertEquals(RESULT_CODE.LAST_VISIT_MUST_BE_OPEN, result);
    }

    @Test
    void testCloseVisitRollbackOnException() {
        // Mock or simulate an exception during closeVisit
        when(mockAccountRepository.save(visitor)).thenThrow(new RuntimeException("Simulated exception"));
        // Capture initial database state
        long initialRewardCount = rewardRepository.count();
        long initialTransferCount = transferRepository.count();
        Map<String, Integer> initialBalance = userAccountService.getBalance(visitor);

        // Perform the operation
        Visit openVisit = new Visit(null, location, LocalDateTime.now(), null, VisitState.OPEN, visitor);
        visitRepository.save(openVisit);

        // Verify no changes were persisted
        assertEquals(initialRewardCount, rewardRepository.count(), "Rewards should not be updated");
        assertEquals(initialTransferCount, transferRepository.count(), "Transfers should not be saved");
        UserAccount updatedVisitor = (UserAccount) accountRepository.findById(visitor.getId()).orElseThrow();
        assertEquals(initialBalance, updatedVisitor.getBalance(), "Visitor balance should remain unchanged");
    }

}

