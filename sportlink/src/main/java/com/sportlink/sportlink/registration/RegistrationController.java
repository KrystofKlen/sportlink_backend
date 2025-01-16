package com.sportlink.sportlink.registration;

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

    @PostMapping("/start-user-registration")
    public ResponseEntity<Void> startUserRegistration(@Valid @ModelAttribute DTO_UserRegistration registrationData) {
        try {
            String otp = registrationService.startRegistration(registrationData);
            try {
                emailSender.sendOtpRegistrationEmail(registrationData.getLoginEmail(), otp);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/complete-user-registration")
    public ResponseEntity<String> completeUserRegistration(@RequestParam String username, @RequestParam String otp) {
        try {
            registrationService.completeRegistration(username, otp);
            return ResponseEntity.ok("User registration completed successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/request-company-registration")
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

}
