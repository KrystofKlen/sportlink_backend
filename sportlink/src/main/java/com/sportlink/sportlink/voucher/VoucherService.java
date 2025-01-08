package com.sportlink.sportlink.voucher;

import com.sportlink.sportlink.security.EncryptionUtil;
import com.sportlink.sportlink.utils.DTO_Adapter;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoucherService {

    private final I_VoucherRepository voucherRepository;
    private final DTO_Adapter adapter;

    public VoucherService(I_VoucherRepository voucherRepository, DTO_Adapter adapter) {
        this.voucherRepository = voucherRepository;
        this.adapter = adapter;
    }

    @Transactional
    public DTO_Voucher saveVoucher(Voucher voucher) {
        voucher.setId(null);
        Voucher savedVoucher = voucherRepository.saveVoucher(voucher);
        return adapter.getDTO_Voucher(savedVoucher);
    }

    public Optional<DTO_Voucher> getVoucher(Long voucherId) {
        Optional<Voucher> voucherOptional = voucherRepository.getVoucher(voucherId);
        return voucherOptional.map(adapter::getDTO_Voucher);
    }

    public Page<DTO_Voucher> getVouchers(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return voucherRepository.findAllByRandomOrder(pageable).map(adapter::getDTO_Voucher);
    }

    // will update only the things that are != null
    @Transactional
    public Voucher updateVoucher(Voucher voucher) {

        Optional<Voucher> existingVoucherOpt = voucherRepository.getVoucher(voucher.getId());
        if (existingVoucherOpt.isEmpty()) {
            throw new RuntimeException("Voucher not found with ID: " + voucher.getId());
        }

        Voucher existingVoucher = existingVoucherOpt.get();

        if(voucher.getTitle() != null){
            existingVoucher.setTitle(voucher.getTitle());
        }
        if(voucher.getDescription() != null){
            existingVoucher.setDescription(voucher.getDescription());
        }
        if(voucher.getCurrency() != null) {
            existingVoucher.setCurrency(voucher.getCurrency());
        }
        if(voucher.getPrice() != null) {
            existingVoucher.setPrice(voucher.getPrice());
        }
        if(voucher.getIssuer() != null) {
            existingVoucher.setIssuer(voucher.getIssuer());
        }
        if(voucher.getExpirationDate() != null) {
            existingVoucher.setExpirationDate(voucher.getExpirationDate());
        }
        if(voucher.getState() != null){
            existingVoucher.setState(voucher.getState());
        }
        if(voucher.getCode() != null){
            try {
                String encryptedCode = EncryptionUtil.encrypt(voucher.getCode());
                existingVoucher.setCode(encryptedCode);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return voucherRepository.saveVoucher(existingVoucher);
    }

    public void deleteVoucher(Long voucherId) {
        voucherRepository.deleteVoucher(voucherId);
    }
}
