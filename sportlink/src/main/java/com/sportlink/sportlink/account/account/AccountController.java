package com.sportlink.sportlink.account.account;

import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.account.user.UserAccountService;
import com.sportlink.sportlink.security.SecurityUtils;
import com.sportlink.sportlink.utils.ImgService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/accounts")
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final UserAccountService userAccountService;

    // Create or Update Account
    @PostMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<Account> saveAccount(@RequestBody Account account) {
        Account savedAccount = accountService.save(account);
        return ResponseEntity.ok(savedAccount);
    }

    // Get Account by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','COMPANY','USER')")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        Optional<Account> account = accountService.findAccountById(id);
        return account.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get Account by Email
    @GetMapping("/email")
    @PreAuthorize("hasAnyRole('ADMIN','COMPANY','USER')")
    public ResponseEntity<Account> getAccountByEmail(@RequestParam String email) {
        Optional<Account> account = accountService.findAccountByEmail(email);
        return account.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete Account by ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','COMPANY','USER')")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    // Set Profile Image UUID
    @PatchMapping("/{id}/profile-image")
    @PreAuthorize("hasAnyRole('ADMIN','COMPANY','USER')")
    public ResponseEntity<Void> setProfileImageUUID(@PathVariable Long id, @RequestParam MultipartFile image) {
        boolean uploaded = accountService.setProfileImgUUID(id, image);
        if (uploaded) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}/profile-image")
    @PreAuthorize("hasAnyRole('ADMIN','COMPANY','USER')")
    public ResponseEntity<Void> deleteProfileImageUUID(@PathVariable Long id) {
        boolean uploaded = accountService.setProfileImgUUID(id, null);
        if (uploaded) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/balance")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Integer>> getBalance() {
        UserAccount user = new UserAccount();
        Map<String, Integer> balance = userAccountService.getBalance(user);
        return new ResponseEntity<>(balance, HttpStatus.OK);
    }

    @PatchMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> changeStatus(@RequestParam Long id, @RequestParam ACCOUNT_STATUS status) {
        Long adminId = SecurityUtils.getCurrentAccountId();
        Optional<Account> account = accountService.findAccountById(id);
        if (account.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (account.get().getStatus() == status) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        if (account.get().getId() == adminId) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        account.get().setStatus(status);
        accountService.save(account.get());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/password/{token}")
    @PreAuthorize("hasAnyRole('ADMIN','COMPANY','USER')")
    public ResponseEntity<Void> changePassword(
            @PathVariable String token,
            @RequestParam String otp,
            @RequestParam String newPassword,
            @RequestParam String loginEmail) {

        boolean result = accountService.changePassword(token, otp, newPassword, loginEmail);
        if (result) {
            return ResponseEntity.ok().build();
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/images/{imgName}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Resource> getImg(String imgName) {
        Optional<Resource> img = ImgService.getImage("DIR", imgName);
        return img.map(resource -> new ResponseEntity<>(resource, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

}
