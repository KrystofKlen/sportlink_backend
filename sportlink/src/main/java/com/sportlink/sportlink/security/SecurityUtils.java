package com.sportlink.sportlink.security;

import com.sportlink.sportlink.account.account.DTO_Account;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityUtils {

    public static Long getCurrentAccountId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof DTO_Account) {
            DTO_Account userDetails = (DTO_Account) authentication.getPrincipal();
            return userDetails.getId();
        }
        throw new IllegalStateException("User is not authenticated");
    }

}
