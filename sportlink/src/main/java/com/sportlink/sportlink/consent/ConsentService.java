package com.sportlink.sportlink.consent;

import com.sportlink.sportlink.account.account.Account;
import com.sportlink.sportlink.account.account.AccountService;
import com.sportlink.sportlink.utils.DTO_Adapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsentService {

    private final ConsentRepository consentRepository;
    private final AgreementRepository agreementRepository;
    private final AccountService accountService;
    private final DTO_Adapter adapter;
    public static final Long GDPR_AGREEMENT_ID = 1L;

    public boolean addConsent(Long accountId, Long agreementId){
        Optional<Account> optAcc = accountService.findAccountById(accountId);
        Optional<Agreement> optAgr = agreementRepository.findById(agreementId);
        if(optAcc.isEmpty() || optAgr.isEmpty()){
            return false;
        }
        Consent consent = new Consent(null, optAcc.get(), optAgr.get(), LocalDateTime.now(), null);
        consent = consentRepository.save(consent);
        log.info("Consent added: " + consent );
        return true;
    }

    public void addAgreement(String text, LocalDate endDate){
        Agreement agreement = new Agreement();
        agreement.setAgreement(text);
        agreement.setEndDate(endDate);
        agreementRepository.save(agreement);
        log.info("Agreement added: " + agreement );
    }

    public List<DTO_Consent> getAccountsConsents(Long accountId){
        List<Consent> consents = consentRepository.getConsentsByAccountId(accountId);
        return consents.stream().map(adapter::getDTO_Consent).toList();
    }

    public String getAgreementText(Long agreementId){
        return agreementRepository.findById(agreementId).orElseThrow().getAgreement();
    }
}
