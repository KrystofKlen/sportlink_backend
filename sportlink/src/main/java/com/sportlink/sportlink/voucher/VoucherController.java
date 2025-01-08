package com.sportlink.sportlink.voucher;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/vouchers")
public class VoucherController {
    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @PostMapping
    public ResponseEntity<DTO_Voucher> createVoucher(@RequestBody Voucher voucher) {
        try {
            DTO_Voucher createdVoucher = voucherService.saveVoucher(voucher);
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

    @GetMapping
    public ResponseEntity<Page<DTO_Voucher>> getVouchers(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        Page<DTO_Voucher> vouchers = voucherService.getVouchers(page, size);
        return ResponseEntity.ok(vouchers);
    }

    @PutMapping("/{voucherId}")
    public ResponseEntity<Voucher> updateVoucher(@PathVariable Long voucherId, @RequestBody Voucher voucher) {
        try {
            voucher.setId(voucherId); // Ensure the ID is set correctly for the update
            Voucher updatedVoucher = voucherService.updateVoucher(voucher);
            return ResponseEntity.ok(updatedVoucher);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{voucherId}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable Long voucherId) {
        try {
            voucherService.deleteVoucher(voucherId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
