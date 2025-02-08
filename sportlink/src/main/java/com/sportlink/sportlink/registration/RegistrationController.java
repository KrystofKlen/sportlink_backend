package com.sportlink.sportlink.registration;

import com.sportlink.sportlink.security.SecurityUtils;
import com.sportlink.sportlink.utils.EmailSender;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/registration")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final EmailSender emailSender;
    private final SecurityUtils securityUtils;

    @PostMapping("/start-user-registration")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Void> startUserRegistration(@RequestBody RegistrationPayload registrationData) {
        try {
            String otp = registrationService.startRegistration(registrationData);
            emailSender.sendOtpRegistrationEmail(registrationData.getLoginEmail(), otp);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException | MessagingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/complete-user-registration")
    @PreAuthorize("permitAll()")
    public ResponseEntity<String> completeUserRegistration(@RequestParam String username, @RequestParam String otp) {
        try {
            registrationService.completeRegistration(username, otp);
            return ResponseEntity.ok("User registration completed successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/request-company-registration")
    @PreAuthorize("permitAll()")
    public ResponseEntity<String> requestCompanyRegistration(@Valid @RequestBody DTO_CompanyRegistration registrationData) {
        try {
            Long id = registrationService.requestCompanyRegistration(registrationData);
            try {
                emailSender.sendAdminCompanyRegistrationRequest("admin", Long.toString(id));
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            return ResponseEntity.ok("Company registration requested successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/location-device")
    @PreAuthorize("hasAnyRole('COMPANY')")
    public ResponseEntity<String> registerLocationDevice(@RequestBody DTO_DeviceRegistration registrationData) {
        try {
            Long accountId = SecurityUtils.getCurrentAccountId();
            registrationService.registerLocationDevice(registrationData, accountId);
            return ResponseEntity.ok("Location device registered successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
