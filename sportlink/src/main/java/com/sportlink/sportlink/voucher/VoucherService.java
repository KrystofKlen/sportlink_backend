package com.sportlink.sportlink.voucher;

import com.sportlink.sportlink.account.I_AccountRepository;
import com.sportlink.sportlink.account.company.CompanyAccount;
import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.currency.Currency;
import com.sportlink.sportlink.currency.I_CurrencyRepository;
import com.sportlink.sportlink.media.ImgService;
import com.sportlink.sportlink.security.EncryptionUtil;
import com.sportlink.sportlink.transfer.I_TransferRepository;
import com.sportlink.sportlink.transfer.Transfer;
import com.sportlink.sportlink.utils.DTO_Adapter;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sportlink.sportlink.voucher.VOUCHER_STATE.IN_OFFER;

@Service
public class VoucherService {

    private final I_CurrencyRepository currencyRepository;
    private final I_AccountRepository accountRepository;
    private final I_VoucherRepository voucherRepository;
    private final DTO_Adapter adapter;
    private final I_TransferRepository transferRepository;

    public VoucherService(I_CurrencyRepository currencyRepository, I_AccountRepository accountRepository, I_VoucherRepository voucherRepository, DTO_Adapter adapter, I_TransferRepository transferRepository) {
        this.currencyRepository = currencyRepository;
        this.accountRepository = accountRepository;
        this.voucherRepository = voucherRepository;
        this.adapter = adapter;
        this.transferRepository = transferRepository;
    }

    @Transactional
    public DTO_Voucher addVoucher(CompanyAccount issuer, DTO_Voucher dto, List<MultipartFile> images) throws Exception {

        List<String> uuids = saveImages(images);

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

        return adapter.getDTO_Voucher(voucherRepository.save(voucher));
    }

    public String revealCode(Long voucherId) throws Exception {
        Voucher voucher = voucherRepository.findById(voucherId).orElseThrow();
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
    public void deleteVoucher(Long voucherId) {
        Optional<Voucher> existingVoucherOpt = voucherRepository.findById(voucherId);
        if (existingVoucherOpt.isEmpty()) {
            return;
        }
        if (existingVoucherOpt.get().getState().equals(VOUCHER_STATE.REDEEMED)) {
            throw new RuntimeException("Voucher state is REDEEMED");
        }
        voucherRepository.deleteById(voucherId);
    }

    @Transactional
    public DTO_Voucher buyVoucher(UserAccount buyer, Long voucherId) {

        try {
            // get wanted voucher and price
            Voucher voucher = voucherRepository.findById(voucherId).get();
            String currencyNeeded = voucher.getCurrency().getName();
            int amountNeeded = voucher.getPrice();

            // check if user has enough money
            int updated = buyer.getBalance().get(currencyNeeded) - amountNeeded;
            if (updated < 0) {
                throw new RuntimeException();
            }

            buyer.getBalance().put(voucher.getCurrency(), updated);

            // add transfer
            Transfer transfer = new Transfer(null, buyer, LocalDateTime.now(), voucher.getCurrency(), voucher.getPrice());
            transferRepository.save(transfer);

            // add voucher
            voucher.setState(VOUCHER_STATE.REDEEMED);
            voucher.setBuyer(buyer);

            return adapter.getDTO_Voucher(voucherRepository.save(voucher));

        } catch (Exception ex) {
            // rollback transaction
            throw new RuntimeException();
        }
    }

    public List<String> saveImages(List<MultipartFile> images) throws Exception {
        List<String> uuids = new ArrayList<>();
        // save images
        for (MultipartFile multipartFile : images) {
            String filename = UUID.randomUUID().toString() + "jpg";
            boolean saved = ImgService.saveImage("DIR", filename, multipartFile);
            if (!saved) {
                throw new Exception();
            }
            uuids.add(filename);
        }
        return uuids;
    }

}
