package com.sportlink.sportlink.account.account;

import com.sportlink.sportlink.security.JwtService;
import com.sportlink.sportlink.security.TOKEN_TYPE;
import com.sportlink.sportlink.utils.DTO_Adapter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class AuthService {

    private final I_AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final DTO_Adapter adapter;

    public DTO_LoginResponse login(String principal, String password) throws AuthenticationException {
        // principal - find by username
        Optional<Account> account = accountRepository.findByUsername(principal);
        if (account.isEmpty()) {
            // principal - find by email
            account = accountRepository.findByEmail(principal);
            if (account.isEmpty()) {
                throw new BadCredentialsException("Invalid username or password");
            }
        }

        DTO_Account dto = adapter.getDTO_Account(account.get());

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(dto.getUsername(), password );

        authenticationManager.authenticate(auth);

        DTO_LoginResponse loginResponse = new DTO_LoginResponse();
        String access = jwtService.generateToken(dto, TOKEN_TYPE.ACCESS);
        String refresh = jwtService.generateToken(dto, TOKEN_TYPE.REFRESH);

        loginResponse.setAccessToken(access);
        loginResponse.setRefreshToken(refresh);
        loginResponse.setAccountId(dto.getId());

        SecurityContextHolder.getContext().setAuthentication(auth);

        log.info("New account login: " + dto.getId());

        return loginResponse;
    }

    public String getNewAccessToken(Long accountId, String refreshToken) throws IllegalArgumentException{
        Account acc = accountRepository.findById(accountId).orElseThrow();
        DTO_Account dtoAccount = adapter.getDTO_Account(acc);
        if (! jwtService.isTokenValid(refreshToken, dtoAccount, TOKEN_TYPE.REFRESH)) {
            throw new IllegalArgumentException();
        }

        // Generate a new access token
        return jwtService.generateToken(dtoAccount, TOKEN_TYPE.ACCESS);
    }
}
