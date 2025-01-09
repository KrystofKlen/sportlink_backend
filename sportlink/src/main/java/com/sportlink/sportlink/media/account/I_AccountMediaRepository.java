package com.sportlink.sportlink.media.account;

import java.util.Optional;

public interface I_AccountMediaRepository {
    AccountMedia save(AccountMedia accountMedia);
    Optional<AccountMedia> findById(Long id);
}
