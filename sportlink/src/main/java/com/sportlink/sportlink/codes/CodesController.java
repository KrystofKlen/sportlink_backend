package com.sportlink.sportlink.codes;

import com.sportlink.sportlink.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/codes")
public class CodesController {

    private final CodesService codesService;

    public CodesController(CodesService codesService) {
        this.codesService = codesService;
    }

    @GetMapping("/otp-for-location")
    @PreAuthorize("hasRole('LOCATION_DEVICE')")
    public ResponseEntity<DTO_Code> requestOTPLocation(@RequestParam long userId) {
        Long accountId = SecurityUtils.getCurrentAccountId();

        String code = codesService.establishLocationOTP(userId, accountId);
        DTO_Code dto = new DTO_Code();
        dto.setCode(code);
        dto.setSecondsExp(58);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PutMapping("/otp-passwd-reset")
    @PreAuthorize("permitAll()")
    public ResponseEntity<String> requestPasswordResetOTP(@RequestParam String accountEmail) {
        try {
            boolean isEligible = codesService.isEligibleForPasswordReset(accountEmail);
            if (!isEligible) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            String token = codesService.sendCodeForOTP(accountEmail);
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
