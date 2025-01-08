package com.sportlink.sportlink.registration;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/start-user-registration")
    public ResponseEntity<String> startUserRegistration(@Valid @ModelAttribute DTO_UserRegistration registrationData) {
        try {
            String otp = registrationService.startRegistration(registrationData);
            return ResponseEntity.ok(otp);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
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
            registrationService.requestCompanyRegistration(registrationData);
            return ResponseEntity.ok("Company registration requested successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
