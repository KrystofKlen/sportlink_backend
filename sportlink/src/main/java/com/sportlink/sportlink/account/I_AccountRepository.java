package com.sportlink.sportlink.account;

import java.util.List;
import java.util.Optional;

public interface I_AccountRepository {
    Account save(Account account);
    Optional<Account> findById(Long id);
    Optional<Account> findByUsername(String username);
    List<Account> findAll();
    void delete(Long id);
}
