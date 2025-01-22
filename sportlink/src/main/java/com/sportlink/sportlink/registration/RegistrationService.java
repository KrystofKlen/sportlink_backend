package com.sportlink.sportlink.registration;

import com.sportlink.sportlink.account.account.Account;
import com.sportlink.sportlink.account.account.AccountService;
import com.sportlink.sportlink.account.company.CompanyAccount;
import com.sportlink.sportlink.account.user.DTO_UserAccount;
import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.account.user.UserAccountService;
import com.sportlink.sportlink.consent.ConsentService;
import com.sportlink.sportlink.currency.Currency;
import com.sportlink.sportlink.currency.I_CurrencyRepository;
import com.sportlink.sportlink.redis.RedisService;
import com.sportlink.sportlink.security.EncryptionUtil;
import com.sportlink.sportlink.utils.PayloadParser;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class RegistrationService {

    private final UserAccountService userAccountService;
    private final EncryptionUtil.SaltGenerator saltGenerator;
    private final PasswordEncoder passwordEncoder;
    private final I_CurrencyRepository currencyRepository;
    private final RedisService redisService;
    private final AccountService accountService;
    private final ConsentService consentService;

    @Transactional
    public String startRegistration(DTO_UserRegistration registrationData) {

        if (!approveUniqueValues(registrationData.getLoginEmail(), registrationData.getUsername())) {
            throw new IllegalStateException("User account already exists");
        }

        // salt passwd
        String salt = saltGenerator.generateSalt();
        String passwd = passwordEncoder.encode(registrationData.getPassword() + salt);
        registrationData.setSalt(salt);
        registrationData.setPassword(passwd);

        // store otp and registration data into redis
        String otp = EncryptionUtil.generateRandomSequence(10);

        String payload = PayloadParser.parseObjectToJson(registrationData);

        redisService.saveValueWithExpiration(registrationData.getLoginEmail(), otp, 2);
        redisService.saveValueWithExpiration(registrationData.getUsername(), payload, 2);

        log.info("Registration started for account: " + registrationData.getUsername());

        return otp;
    }

    @Transactional
    public void completeRegistration(String username, String otp) {

        // retrieve from redis
        String payload = redisService.getValue(username);
        DTO_UserRegistration userRegistration = (DTO_UserRegistration) PayloadParser.parseJsonToObject(payload, DTO_UserRegistration.class);
        String email = userRegistration.getLoginEmail();
        String expectedOTP = redisService.getValue(email);
        if (!expectedOTP.equals(otp)) {
            throw new IllegalStateException("Invalid OTP");
        }

        // create object
        UserAccount userAccount = new UserAccount(
                email,
                username,
                userRegistration.getPassword(),
                userRegistration.getSalt(),
                userRegistration.getFirstName(),
                userRegistration.getLastName(),
                userRegistration.getDateOfBirth());

        Account account = accountService.save(userAccount);
        consentService.addConsent(account.getId(), ConsentService.GDPR_AGREEMENT_ID);

        log.info("Account added - username:" + userAccount.getUsername() + " id:" + userAccount.getId() );
    }

    @Transactional
    public Long requestCompanyRegistration(DTO_CompanyRegistration registrationData) {

        if (!approveUniqueValues(registrationData.getLoginEmail(), registrationData.getUsername(), registrationData.getCurrencyName())) {
            throw new IllegalStateException("User account already exists");
        }

        String salt = saltGenerator.generateSalt();
        String passwd = passwordEncoder.encode(registrationData.getPassword() + salt);

        // create not approved account
        CompanyAccount companyAccount = new CompanyAccount(
                registrationData.getLoginEmail(),
                registrationData.getUsername(),
                passwd,
                salt,
                registrationData.getName(),
                registrationData.getAddress(),
                registrationData.getPhone(),
                registrationData.getContactEmail(),
                registrationData.getWebsiteUrl()
        );

        // save
        Account saved = accountService.save(companyAccount);

        // create currency
        Currency currency = new Currency();
        currency.setIssuer(saved);
        currency.setName(registrationData.getCurrencyName());
        currencyRepository.save(currency);

        log.info("Company account registration requested - username:" + saved.getUsername() + " id:" + saved.getId() );

        return saved.getId();
    }

    private boolean approveUniqueValues(String email, String username, String currency) {
        // check if username and email are free
        Optional<DTO_UserAccount> sameEmailUser = userAccountService.findByEmail(email);
        Optional<DTO_UserAccount> sameUsernameUser = userAccountService.findByUsername(username);
        Optional<Currency> sameCurrency = currencyRepository.findCurrencyByName(currency);

        boolean emailFree = sameEmailUser.isEmpty() && redisService.getValue(email) == null;
        boolean usernameFree = sameUsernameUser.isEmpty() && redisService.getValue(username) == null;
        boolean currencyFree = sameCurrency.isEmpty() && redisService.getValue(currency) == null;

        return emailFree && usernameFree && currencyFree;
    }

    private boolean approveUniqueValues(String email, String username) {
        // check if username and email are free
        Optional<DTO_UserAccount> sameEmailUser = userAccountService.findByEmail(email);
        Optional<DTO_UserAccount> sameUsernameUser = userAccountService.findByUsername(username);

        boolean emailFree = sameEmailUser.isEmpty() && redisService.getValue(email) == null;
        boolean usernameFree = sameUsernameUser.isEmpty() && redisService.getValue(username) == null;

        return emailFree && usernameFree;
    }
}
