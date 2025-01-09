package com.sportlink.sportlink.media;

import com.sportlink.sportlink.media.account.AccountMediaService;
import com.sportlink.sportlink.media.location.LocationMediaService;
import com.sportlink.sportlink.media.voucher.VoucherMediaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/media")
public class MediaController {

    private final AccountMediaService accountMediaService;
    private final LocationMediaService locationMediaService;
    private final VoucherMediaService voucherMediaService;

    public MediaController(AccountMediaService accountMediaService,
                           LocationMediaService locationMediaService,
                           VoucherMediaService voucherMediaService) {
        this.accountMediaService = accountMediaService;
        this.locationMediaService = locationMediaService;
        this.voucherMediaService = voucherMediaService;
    }

    // Account Media Endpoints
    @PostMapping("/account/{accountId}/image")
    public ResponseEntity<String> saveAccountImage(@PathVariable Long accountId, @RequestParam String img) {
        String savedImage = accountMediaService.saveUserAccountImage(accountId, img);
        return ResponseEntity.ok(savedImage);
    }

    @GetMapping("/account/{accountId}/image")
    public ResponseEntity<String> getAccountImage(@PathVariable Long accountId) {
        String profilePic = accountMediaService.getProfilePic(accountId);
        return ResponseEntity.ok(profilePic);
    }

    @DeleteMapping("/account/{accountId}/image")
    public ResponseEntity<Void> deleteAccountImage(@PathVariable Long accountId) {
        accountMediaService.deleteAccountImage(accountId);
        return ResponseEntity.noContent().build();
    }

    // Location Media Endpoints
    @PostMapping("/location/{locationId}/image")
    public ResponseEntity<Void> saveLocationImage(@PathVariable Long locationId, @RequestParam String img) {
        locationMediaService.saveLocationImage(locationId, img);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/location/{locationId}/images")
    public ResponseEntity<List<String>> getLocationImages(@PathVariable Long locationId) {
        List<String> images = locationMediaService.findByIdImages(locationId);
        return ResponseEntity.ok(images);
    }

    @DeleteMapping("/location/{locationId}/image")
    public ResponseEntity<Void> deleteLocationImage(@PathVariable Long locationId, @RequestParam String imageUUID) {
        locationMediaService.deleteLocationImage(locationId, imageUUID);
        return ResponseEntity.noContent().build();
    }

    // Voucher Media Endpoints
    @PostMapping("/voucher/{voucherId}/image")
    public ResponseEntity<Void> saveVoucherImage(@PathVariable Long voucherId, @RequestParam String img) {
        voucherMediaService.saveVoucherImage(voucherId, img);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/voucher/{voucherId}/images")
    public ResponseEntity<List<String>> getVoucherImages(@PathVariable Long voucherId) {
        List<String> images = voucherMediaService.getImages(voucherId);
        return ResponseEntity.ok(images);
    }

    @DeleteMapping("/voucher/{voucherId}/image")
    public ResponseEntity<Void> deleteVoucherImage(@PathVariable Long voucherId, @RequestParam String imageUUID) {
        voucherMediaService.deleteVoucherImage(voucherId, imageUUID);
        return ResponseEntity.noContent().build();
    }
}
