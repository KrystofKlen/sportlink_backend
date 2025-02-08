package com.sportlink.sportlink.registration;

import com.sportlink.sportlink.account.account.Account;
import com.sportlink.sportlink.account.account.AccountService;
import com.sportlink.sportlink.account.account.DTO_Account;
import com.sportlink.sportlink.account.company.CompanyAccount;
import com.sportlink.sportlink.account.device.LocationDevice;
import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.account.user.UserAccountService;
import com.sportlink.sportlink.consent.ConsentService;
import com.sportlink.sportlink.currency.Currency;
import com.sportlink.sportlink.currency.I_CurrencyRepository;
import com.sportlink.sportlink.location.I_LocationRepository;
import com.sportlink.sportlink.location.Location;
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

    private final EncryptionUtil.SaltGenerator saltGenerator;
    private final PasswordEncoder passwordEncoder;
    private final I_CurrencyRepository currencyRepository;
    private final RedisService redisService;
    private final AccountService accountService;
    private final ConsentService consentService;
    private final I_LocationRepository locationRepository;

    @Transactional
    public String startRegistration(RegistrationPayload registrationData) {

        if (!approveUniqueValues(registrationData.getLoginEmail(), registrationData.getUsername())) {
            throw new IllegalStateException("User account already exists");
        }

        // salt passwd
        String salt = saltGenerator.generateSalt();
        String passwd = passwordEncoder.encode(registrationData.getPassword() + salt);
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
        RegistrationPayload userRegistration = (RegistrationPayload) PayloadParser.parseJsonToObject(payload, RegistrationPayload.class);
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

    @Transactional
    public void registerLocationDevice(DTO_DeviceRegistration registrationData, Long accountRequestingId) {
        boolean unique = approveUniqueValues(registrationData.getLoginEmail(), registrationData.getUsername());
        if(!unique){
            throw new IllegalStateException("User account already exists");
        }
        LocationDevice locationDevice = new LocationDevice();
        locationDevice.setLoginEmail(registrationData.getLoginEmail());
        locationDevice.setUsername(registrationData.getUsername());
        String encryptedPasswd = passwordEncoder.encode(registrationData.password);
        locationDevice.setPassword(encryptedPasswd);
        Location location = locationRepository.findById(registrationData.locationId).orElseThrow();
        if( location.getIssuer().getId() != accountRequestingId ) {
            throw new IllegalStateException("Invalid location");
        }
        locationDevice.setLocation(location);
        accountService.save(locationDevice);
    }

    private boolean approveUniqueValues(String email, String username, String currency) {
        // check if username and email are free
        Optional<Account> sameEmailUser = accountService.findAccountByEmail(email);
        Optional<DTO_Account> sameUsernameUser = accountService.findDTOAccountByUsername(username);
        Optional<Currency> sameCurrency = currencyRepository.findCurrencyByName(currency);

        boolean emailFree = sameEmailUser.isEmpty() && redisService.getValue(email) == null;
        boolean usernameFree = sameUsernameUser.isEmpty() && redisService.getValue(username) == null;
        boolean currencyFree = sameCurrency.isEmpty() && redisService.getValue(currency) == null;

        return emailFree && usernameFree && currencyFree;
    }

    private boolean approveUniqueValues(String email, String username) {
        // check if username and email are free
        Optional<Account> sameEmailUser = accountService.findAccountByEmail(email);
        Optional<DTO_Account> sameUsernameUser = accountService.findDTOAccountByUsername(username);

        boolean emailFree = sameEmailUser.isEmpty() && redisService.getValue(email) == null;
        boolean usernameFree = sameUsernameUser.isEmpty() && redisService.getValue(username) == null;

        return emailFree && usernameFree;
    }
}
