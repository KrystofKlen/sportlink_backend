package com.sportlink.sportlink.media.account;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountMediaService {

    private static final String BASE_URL = "x";
    public static final String PATH = "y";

    private final I_AccountMediaRepository accountMediaRepository;

    public AccountMediaService(I_AccountMediaRepository accountMediaRepository) {
        this.accountMediaRepository = accountMediaRepository;
    }

    public String saveUserAccountImage(Long accountId, String img) {
        Optional<AccountMedia> opt = accountMediaRepository.findById(accountId);
        AccountMedia accountMedia;
        accountMedia = opt.orElseGet(AccountMedia::new);
        accountMedia.setId(accountId);
        accountMedia.setProfileImgName(img);
        return accountMediaRepository.save(accountMedia).getProfileImgName();
    }

    public String getProfilePic(Long accountId) {
        Optional<AccountMedia> opt = accountMediaRepository.findById(accountId);
        return opt.map(accountMedia -> BASE_URL + PATH + accountMedia.getProfileImgName())
                .orElse("defaultPic");
    }

    public void deleteAccountImage(Long accountId) {
        Optional<AccountMedia> opt = accountMediaRepository.findById(accountId);
        if(opt.isEmpty()) {
            return;
        }
        opt.get().setProfileImgName("defaultPic");
        accountMediaRepository.save(opt.get());
    }
}
