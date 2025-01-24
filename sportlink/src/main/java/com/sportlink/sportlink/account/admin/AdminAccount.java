package com.sportlink.sportlink.account.admin;

import com.sportlink.sportlink.account.account.Account;
import com.sportlink.sportlink.account.ROLE;
import jakarta.persistence.Entity;

@Entity
public class AdminAccount extends Account {
    public AdminAccount() {
        super();
        setRole(ROLE.ROLE_ADMIN);
    }
}
