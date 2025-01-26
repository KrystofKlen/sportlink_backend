package com.sportlink.sportlink.seeder;

import com.sportlink.sportlink.account.account.AccountService;
import com.sportlink.sportlink.account.company.CompanyAccount;
import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.currency.Currency;
import com.sportlink.sportlink.currency.I_CurrencyRepository;
import com.sportlink.sportlink.location.ACTIVITY;
import com.sportlink.sportlink.location.DTO_Location;
import com.sportlink.sportlink.location.LocationService;
import com.sportlink.sportlink.reward.DTO_Reward;
import com.sportlink.sportlink.verification.location.LOCATION_VERIFICATION_STRATEGY;
import com.sportlink.sportlink.verification.reward.REWARD_CONDITION;
import com.sportlink.sportlink.voucher.DTO_Voucher;
import com.sportlink.sportlink.voucher.VOUCHER_STATE;
import com.sportlink.sportlink.voucher.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@Profile("seeder")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AccountService accountService;
    private final I_CurrencyRepository i_CurrencyRepository;
    private final LocationService locationService;
    private final VoucherService voucherService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // add accounts
        List<UserAccount> userAccounts = createUsers(10);
        userAccounts.forEach(userAccount -> {
            accountService.save(userAccount);
        });

        // add company
        CompanyAccount company = createCompanies(10).getFirst();
        company = (CompanyAccount) accountService.save(company);

        // add currency
        Currency currency = new Currency();
        currency.setName("currency - " + company.getId());
        currency.setIssuer(company);
        currency = i_CurrencyRepository.save(currency);

        // add locations
        List<DTO_Location> locations = generateLocations(15);
        List<DTO_Location> savedLocations = new ArrayList<>();
        for (DTO_Location location : locations) {
            DTO_Location saved = locationService.saveLocation(location, company.getId());
            savedLocations.add(saved);
        }

        // add rewards
        List<DTO_Reward> rewards = generateRewards(3,currency);
        for(DTO_Location saved : savedLocations) {
            for (DTO_Reward reward : rewards) {
                locationService.addReward(saved.getId(), reward);
            }
        }

        // add vouchers
        List<DTO_Voucher> vouchers = generateVouchers(15,currency);
        for(DTO_Voucher voucher : vouchers) {
            voucherService.addVoucher(company.getId(), voucher, List.of());
        }
    }

    private List<UserAccount> createUsers(int count) {
        List<UserAccount> users = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String encryptedPasswd = passwordEncoder.encode("password");
            UserAccount user = new UserAccount(
                    "user" + i + "@example.com",
                    "username" + i,
                    encryptedPasswd,
                    "FirstName" + i,
                    "LastName" + i,
                    new Date()
            );
            user.setProfilePicUUID("profile.jpg");
            users.add(user);
        }
        return users;
    }

    private List<CompanyAccount> createCompanies(int count) {
        List<CompanyAccount> companies = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String encryptedPasswd = passwordEncoder.encode("password");
            CompanyAccount company = new CompanyAccount(
                    "company" + i + "@example.com",  // loginEmail
                    "company" + i,                  // username
                    encryptedPasswd,        // passwordEncrypted
                    "CompanyName" + i,              // name
                    "123 Company Street " + i,      // address
                    "123-456-789" + i,              // phone
                    "contact@company" + i + ".com", // contactEmail
                    "https://company" + i + ".com"  // websiteUrl
            );
            company.setProfilePicUUID("profile.jpg");
            companies.add(company);
        }
        return companies;
    }

    public static List<DTO_Location> generateLocations(int count) {
        Random random = new Random();
        List<DTO_Location> locations = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            Set<ACTIVITY> activities = new HashSet<>();
            activities.add(ACTIVITY.values()[random.nextInt(ACTIVITY.values().length)]);

            Set<LOCATION_VERIFICATION_STRATEGY> verificationStrategies = new HashSet<>();
            verificationStrategies.add(LOCATION_VERIFICATION_STRATEGY.USER_WITHIN_RADIUS);

            DTO_Location location = new DTO_Location(
                    null,
                    "Location " + i,
                    "Address " + i,
                    "Description for Location " + i,
                    activities,
                    Math.random() * 180 - 90,
                    Math.random() * 360 - 180,
                    verificationStrategies,
                    List.of("gym.jpg")
            );
            locations.add(location);
        }
        return locations;
    }

    public static List<DTO_Reward> generateRewards(int count, Currency currency) {
        Random random = new Random();
        List<DTO_Reward> rewards = new ArrayList<>();
        for (int j = 1; j <= count; j++) {
            DTO_Reward reward = new DTO_Reward();
            reward.setAmount(10 * j);
            reward.setTotalClaimsLimit(100);
            reward.setTotalClaimsCount(0);
            reward.setMonthClaimsLimit(10);
            reward.setMonthClaimsCount(0);
            reward.setMinMinutesSpent(30);
            reward.setCurrency(currency.getName());
            reward.setRewardConditions(
                    List.of(REWARD_CONDITION.MONTHLY_CLAIMS_LIMIT)
            );
            rewards.add(reward);
        }
        return rewards;
    }

    public static List<DTO_Voucher> generateVouchers(int count, Currency currency){
        Random random = new Random();
        List<DTO_Voucher> vouchers = new ArrayList<>();
        for(int i = 0; i<count; i++){
            DTO_Voucher voucher = new DTO_Voucher(
                    null,
                    "Voucher for " + currency.getName(),
                    "Special offer with " + currency.getName(),
                    currency.getName(),
                    15,
                    LocalDate.now().plusMonths(5),
                    VOUCHER_STATE.IN_OFFER,
                    "ABCD",
                    List.of("shoes.jpg")
            );
            vouchers.add(voucher);
        }
        return vouchers;
    }
}
