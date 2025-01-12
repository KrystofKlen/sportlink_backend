package com.sportlink.sportlink.voucher;

import com.sportlink.sportlink.account.company.CompanyAccount;
import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.media.ImgService;
import com.sportlink.sportlink.utils.RESULT_CODE;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static com.sportlink.sportlink.utils.RESULT_CODE.INSUFFICIENT_FUNDS;
import static com.sportlink.sportlink.utils.RESULT_CODE.REDEEMED;

@RestController
@RequestMapping("/api/v1/vouchers")
@AllArgsConstructor
public class VoucherController {
    private final VoucherService voucherService;
    private final RedeemTransactionManager redeemTransactionManager;


    @PostMapping
    public ResponseEntity<DTO_Voucher> createVoucher(@RequestBody DTO_Voucher voucher, @RequestBody List<MultipartFile> images) {
        try {
            Long compId = 8L;
            CompanyAccount account = new CompanyAccount();
            DTO_Voucher createdVoucher = voucherService.addVoucher(account, voucher, images);
            return new ResponseEntity<>(createdVoucher, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{voucherId}")
    public ResponseEntity<DTO_Voucher> getVoucher(@PathVariable Long voucherId) {
        Optional<DTO_Voucher> voucher = voucherService.getVoucher(voucherId);
        return voucher.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/reveal/")
    public ResponseEntity<String> revealVoucherCode(@PathVariable Long voucherId) {
        try {
            String result = voucherService.revealCode(voucherId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<Page<DTO_Voucher>> getVouchersInOffer(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        Page<DTO_Voucher> vouchers = voucherService.getVouchersInOffer(page, size);
        return ResponseEntity.ok(vouchers);
    }

    // ADMIN ONLY
    @DeleteMapping("/{voucherId}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable Long voucherId) {
        try {
            voucherService.deleteVoucher(voucherId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/buyer")
    public ResponseEntity<List<DTO_Voucher>> getBuyersVouchers() {
        Long buyerId = 1L;
        List<DTO_Voucher> result = voucherService.getBuyersVouchers(buyerId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/issuer")
    public ResponseEntity<List<DTO_Voucher>> getIssuersVouchers() {
        Long issuerId = 1L;
        List<DTO_Voucher> result = voucherService.getIssuersVouchers(issuerId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/redeem/{voucherId}")
    public ResponseEntity<RESULT_CODE> redeemVoucher(@PathVariable Long voucherId) {
        UserAccount user = new UserAccount();
        try {
            RESULT_CODE result = redeemTransactionManager.redeemVoucher(voucherId, user);
            switch (result) {
                case INSUFFICIENT_FUNDS -> {
                    return new ResponseEntity<>(INSUFFICIENT_FUNDS, HttpStatus.BAD_REQUEST);
                }
                case REDEEMED -> {
                    return new ResponseEntity<>(REDEEMED, HttpStatus.OK);
                }
                default -> {
                    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/images/{imgName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imgName) {
        Optional<Resource> image = ImgService.getImage("DIR", imgName);
        return image.map(resource -> new ResponseEntity<>(resource, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }
}
