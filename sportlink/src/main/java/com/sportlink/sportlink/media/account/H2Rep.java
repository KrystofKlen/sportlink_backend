package com.sportlink.sportlink.media.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class H2Rep implements I_AccountMediaRepository {

    private final JpaRepository<AccountMedia, Long> accountMediaRep;

    public H2Rep(JpaRepository<AccountMedia, Long> accountMediaRep) {
        this.accountMediaRep = accountMediaRep;
    }

    @Override
    public AccountMedia save(AccountMedia accountMedia) {
        return accountMediaRep.save(accountMedia);
    }

    @Override
    public Optional<AccountMedia> findById(Long id) {
        return accountMediaRep.findById(id);
    }


}
