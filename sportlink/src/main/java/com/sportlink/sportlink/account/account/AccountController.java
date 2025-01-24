package com.sportlink.sportlink.account.account;

import com.sportlink.sportlink.account.company.CompanyAccount;
import com.sportlink.sportlink.account.company.DTO_CompanyAccountDetails;
import com.sportlink.sportlink.account.user.DTO_UserAccountDetails;
import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.account.user.UserAccountService;
import com.sportlink.sportlink.security.SecurityUtils;
import com.sportlink.sportlink.utils.DTO_Adapter;
import com.sportlink.sportlink.utils.ImgService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final ImgService imgService;
    private final AuthService authService;
    private final DTO_Adapter adapter;

    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DTO_UserAccountDetails> getUserAccountDetails(){
        Long id = SecurityUtils.getCurrentAccountId();
        try {
            UserAccount userAccount = (UserAccount) accountService.findAccountById(id).orElseThrow();
            DTO_UserAccountDetails dto_UserAccountDetails = adapter.getDTO_UserAccountDetails(userAccount);
            return new ResponseEntity<>(dto_UserAccountDetails, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/company/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<DTO_CompanyAccountDetails> getCompanyAccountDetails(){
        Long id = SecurityUtils.getCurrentAccountId();
        try {
            CompanyAccount companyAccount = (CompanyAccount) accountService.findAccountById(id).orElseThrow();
            DTO_CompanyAccountDetails dto_CompanyAccountDetails = adapter.getDTO_CompanyAccountDetails(companyAccount);
            return new ResponseEntity<>(dto_CompanyAccountDetails, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete Account by ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','COMPANY','USER')")
    public ResponseEntity<Void> deleteAccount() {
        Long id = SecurityUtils.getCurrentAccountId();
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/balance")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Integer>> getBalance() {
        Long accountIdId = SecurityUtils.getCurrentAccountId();
        try {
            UserAccount user = (UserAccount) accountService.findAccountById(accountIdId).orElseThrow();
            Map<String, Integer> balance = userAccountService.getBalance(user);
            return new ResponseEntity<>(balance, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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
        Optional<Resource> img = imgService.getImage(imgService.PATH_ACCOUNT, imgName);
        if(img.isPresent()){
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(img.get());
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    // Change Profile Image
    @PatchMapping("/{id}/profile-image")
    @PreAuthorize("hasAnyRole('ADMIN','COMPANY','USER')")
    public ResponseEntity<Void> setProfileImage(@PathVariable Long id, @RequestParam MultipartFile image) {
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

    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<DTO_LoginResponse> login(@ModelAttribute DTO_LoginRequest request) {
        try {
            DTO_LoginResponse tokens = authService.login(request.principal, request.password);
            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/refresh-token")
    @PreAuthorize("hasAnyRole('USER','COMPANY')")
    public ResponseEntity<String> refreshToken(@RequestParam String refreshToken){
        try {
            // Check if the refresh token is valid and not expired
            Long accountId = SecurityUtils.getCurrentAccountId();
            String newAccessToken = authService.getNewAccessToken(accountId, refreshToken);
            return ResponseEntity.ok(newAccessToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
