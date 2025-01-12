package com.sportlink.sportlink.codes;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/codes")
public class CodesController {

    private final CodesService codesService;

    public CodesController(CodesService codesService) {
        this.codesService = codesService;
    }

    @GetMapping("/otp-for-location")
    public ResponseEntity<DTO_Code> requestOTPLocation(@RequestParam long userId, @RequestParam long locationId) {
        String code = codesService.sendLocationOTP(userId, locationId);
        DTO_Code dto = new DTO_Code();
        dto.setCode(code);
        dto.setSecondsExp(1 * 60 - 2);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PutMapping("/otp-passwd-reset")
    public ResponseEntity<String> requestPasswordResetOTP(@RequestParam long userId) {
        try {
            String token = codesService.sendCodeForOTP(userId);
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
