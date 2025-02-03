package com.sportlink.sportlink.voucher;

import com.sportlink.sportlink.account.ROLE;
import com.sportlink.sportlink.account.account.Account;
import com.sportlink.sportlink.account.account.I_AccountRepository;
import com.sportlink.sportlink.account.company.CompanyAccount;
import com.sportlink.sportlink.currency.Currency;
import com.sportlink.sportlink.currency.I_CurrencyRepository;
import com.sportlink.sportlink.redis.RedisService;
import com.sportlink.sportlink.utils.ImgService;
import com.sportlink.sportlink.security.EncryptionUtil;
import com.sportlink.sportlink.utils.DTO_Adapter;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sportlink.sportlink.voucher.VOUCHER_STATE.EXPIRED;
import static com.sportlink.sportlink.voucher.VOUCHER_STATE.IN_OFFER;

@Service
@Slf4j
@AllArgsConstructor
public class VoucherService {

    private final I_CurrencyRepository currencyRepository;
    private final I_AccountRepository accountRepository;
    private final I_VoucherRepository voucherRepository;
    private final DTO_Adapter adapter;
    private final ImgService imgService;
    private final RedisService redisService;


    @Transactional
    public DTO_Voucher addVoucher(Long issuerId, DTO_Voucher dto, List<MultipartFile> images) throws Exception {

        List<String> uuids = saveImages(images);
        CompanyAccount issuer = (CompanyAccount) accountRepository.findById(issuerId).orElseThrow();

        // add voucher
        Currency currency = currencyRepository.findByIssuer(issuer.getId()).orElseThrow();

        Voucher voucher = new Voucher();
        voucher.setTitle(dto.getTitle());
        voucher.setDescription(dto.getDescription());
        voucher.setIssuer(issuer);
        voucher.setCurrency(currency);
        voucher.setPrice(dto.getPrice());
        voucher.setExpirationDate(dto.getExpirationDate());
        voucher.setState(IN_OFFER);
        voucher.setImagesUUID(uuids);
        // encrypt and protect voucher code
        String code = EncryptionUtil.encrypt(dto.getCode());
        voucher.setCode(code);

        Voucher saved = voucherRepository.save(voucher);
        log.info("Voucher added: issuerId = " + issuer.getId() + " , saved voucherId = " + saved.getId());

        return adapter.getDTO_Voucher(saved);
    }

    public String revealCode(Long voucherId, Long userId) throws Exception {
        Voucher voucher = voucherRepository.findById(voucherId).orElseThrow();
        if( voucher.getBuyer().getId() != userId){
            throw new Exception();
        }
        log.info("Code revealed: userId = " + userId + " voucherId = " + voucherId);
        return EncryptionUtil.decrypt(voucher.getCode());
    }

    public List<DTO_Voucher> getBuyersVouchers(Long buyerId) {
        return voucherRepository.findById(buyerId).map(adapter::getDTO_Voucher).stream().toList();
    }

    public List<DTO_Voucher> getIssuersVouchers(Long issuerId) {
        return voucherRepository.getIssuersVouchers(issuerId).stream().map(adapter::getDTO_Voucher).toList();
    }

    public Optional<DTO_Voucher> getVoucher(Long voucherId) {
        Optional<Voucher> voucherOptional = voucherRepository.findById(voucherId);
        return voucherOptional.map(adapter::getDTO_Voucher);
    }

    public Page<DTO_Voucher> getVouchersInOffer(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return voucherRepository.findByState(pageable, VOUCHER_STATE.IN_OFFER).map(adapter::getDTO_Voucher);
    }

    @Transactional
    public void deleteVoucher(Long voucherId, Long accountRequestingId) {

        Optional<Voucher> existingVoucherOpt = voucherRepository.findById(voucherId);
        if (existingVoucherOpt.isEmpty()) {
            return;
        }

        if (existingVoucherOpt.get().getState().equals(VOUCHER_STATE.BOUGHT)) {
            throw new RuntimeException("Voucher state is BOUGHT");
        }

        Account acc = accountRepository.findById(accountRequestingId).orElseThrow();
        if(acc.getRole().equals(ROLE.ROLE_ADMIN)){
            voucherRepository.deleteById(voucherId);
        } else if (acc.getRole().equals(ROLE.ROLE_COMPANY)) {

            Long issuerId = existingVoucherOpt.get().getIssuer().getId();
            if(accountRequestingId != issuerId){
                throw new RuntimeException("Not authorized");
            }

            voucherRepository.deleteById(voucherId);
        } else {
            throw new RuntimeException("Not authorized");
        }
        log.info("Voucher deleted: " + existingVoucherOpt.get());
    }

    public List<String> saveImages(List<MultipartFile> images) throws Exception {
        List<String> uuids = new ArrayList<>();
        // save images
        for (MultipartFile multipartFile : images) {
            String filename = UUID.randomUUID().toString() + "jpg";
            boolean saved = imgService.saveImage(imgService.PATH_VOUCHER, filename, multipartFile);
            if (!saved) {
                throw new Exception();
            }
            uuids.add(filename);
        }
        return uuids;
    }

    @Scheduled(cron = "0 0 0 * * ?") // Runs at midnight every day
    @Transactional
    public void expireVouchers() {
        LocalDate today = LocalDate.now();
        List<Voucher> allVouchers = voucherRepository.findAll();
        List<Voucher> expiredVouchers = new ArrayList<>();
        allVouchers.forEach(voucher -> {
            if(!voucher.getState().equals(EXPIRED) && voucher.getExpirationDate().isBefore(today)){
                expiredVouchers.add(voucher);
            }
        });

        expiredVouchers.forEach(voucher -> {
            voucher.setState(VOUCHER_STATE.EXPIRED);
        });

        voucherRepository.saveAll(expiredVouchers);
    }

    public String createOTP(Long voucherId, Long accountRequestingId) {
        Voucher voucher = voucherRepository.findById(voucherId).orElseThrow();
        if(voucher.getBuyer() == null || voucher.getBuyer().getId() != accountRequestingId){
            throw new RuntimeException("Not authorized");
        }
        String key = "VOUCHER:"+voucherId;
        String value = EncryptionUtil.generateRandomSequence(10);
        redisService.saveValueWithExpiration(key,value,1);
        return value;
    }
}
