package com.sportlink.sportlink.account.user;

import com.sportlink.sportlink.account.account.Account;
import com.sportlink.sportlink.account.account.AccountService;
import com.sportlink.sportlink.account.account.I_AccountRepository;
import com.sportlink.sportlink.currency.Currency;
import com.sportlink.sportlink.redis.RedisService;
import com.sportlink.sportlink.security.EncryptionUtil;
import com.sportlink.sportlink.utils.DTO_Adapter;
import com.sportlink.sportlink.utils.ImgService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserAccountService {

    private final DTO_Adapter adapter;

    public Map<String, Integer> getBalance(UserAccount account) {
        Map<Currency,Integer> balance = account.getBalance();
        return adapter.getDTO_Balance(balance);
    }
}
