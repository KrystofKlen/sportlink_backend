package com.sportlink.sportlink.registration;

import com.sportlink.sportlink.account.Account;
import com.sportlink.sportlink.account.AccountService;
import com.sportlink.sportlink.account.company.CompanyAccount;
import com.sportlink.sportlink.account.user.DTO_UserAccount;
import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.account.user.UserAccountService;
import com.sportlink.sportlink.inventory.user.I_UserInventoryRepository;
import com.sportlink.sportlink.inventory.user.UserInventory;
import com.sportlink.sportlink.redis.RedisService;
import com.sportlink.sportlink.security.EncryptionUtil;
import com.sportlink.sportlink.utils.PayloadParser;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegistrationService {

    private final UserAccountService userAccountService;
    private final EncryptionUtil.SaltGenerator saltGenerator;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final RedisService redisService;
    private final I_UserInventoryRepository inventoryInventory;
    private static final int OPT_EXP_MIN = 2;
    @Autowired
    private AccountService accountService;


    public RegistrationService(
            UserAccountService userAccountService,
            EncryptionUtil.SaltGenerator saltGenerator,
            PasswordEncoder passwordEncoder,
            RedisService redisService,
            I_UserInventoryRepository inventoryInventory) {
        this.userAccountService = userAccountService;
        this.saltGenerator = saltGenerator;
        this.passwordEncoder = passwordEncoder;
        this.redisService = redisService;
        this.inventoryInventory = inventoryInventory;
    }

    @Transactional
    public String startRegistration(DTO_UserRegistration registrationData) {

        if (!approveEmailAndUsername(registrationData.getLoginEmail(), registrationData.getUsername())) {
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

        redisService.saveValueWithExpiration(registrationData.getLoginEmail(), otp, OPT_EXP_MIN);
        redisService.saveValueWithExpiration(registrationData.getUsername(), payload, OPT_EXP_MIN);

        // return OTP
        return otp;
    }

    @Transactional
    public UserInventory completeRegistration(String username, String otp) {

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

        // set up inventory
        UserInventory userInventory = new UserInventory();
        userInventory.setOwner(userAccount);

        // store inventory
        UserInventory inventory = inventoryInventory.save(userInventory);
        return userInventory;
    }

    @Transactional
    public void requestCompanyRegistration(DTO_CompanyRegistration registrationData) {

        if (!approveEmailAndUsername(registrationData.getLoginEmail(), registrationData.getUsername())) {
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
                registrationData.getWebsiteUrl(),
                false
        );
        // save
        Account account = accountService.save(companyAccount);
    }

    private boolean approveEmailAndUsername(String email, String username) {
        // check if username and email are free
        Optional<DTO_UserAccount> sameEmailUser = userAccountService.findByEmail(email);
        Optional<DTO_UserAccount> sameUsernameUser = userAccountService.findByUsername(username);
        boolean emailFree = sameEmailUser.isEmpty() && redisService.getValue(email) == null;
        boolean usernameFree = sameUsernameUser.isEmpty() && redisService.getValue(username) == null;

        return emailFree && usernameFree;
    }
}
