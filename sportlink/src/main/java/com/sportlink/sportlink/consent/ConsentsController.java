package com.sportlink.sportlink.consent;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/consent")
@RequiredArgsConstructor
public class ConsentsController {

    private final ConsentService consentService;

    /**
     * Add a new agreement.
     *
     * @param text    Agreement text
     * @param endDate End date of the agreement
     * @return ResponseEntity with success message
     */
    @PostMapping("/agreement")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addAgreement(@RequestParam String text, @RequestParam String endDate) {
        LocalDate parsedEndDate = LocalDate.parse(endDate);
        consentService.addAgreement(text, parsedEndDate);
        return ResponseEntity.ok("Agreement added successfully.");
    }

    /**
     * Get all consents for a given account.
     *
     * @param accountId ID of the account
     * @return List of DTO_Consent objects
     */
    @GetMapping("/{accountId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<DTO_Consent>> getAccountsConsents(@PathVariable Long accountId) {
        List<DTO_Consent> consents = consentService.getAccountsConsents(accountId);
        return ResponseEntity.ok(consents);
    }
}
