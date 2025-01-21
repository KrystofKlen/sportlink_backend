package com.sportlink.sportlink.visit;

import com.sportlink.sportlink.account.account.AccountService;
import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.location.DTO_Location;
import com.sportlink.sportlink.location.LocationService;
import com.sportlink.sportlink.security.SecurityUtils;
import com.sportlink.sportlink.utils.RESULT_CODE;
import com.sportlink.sportlink.verification.location.DTO_LocationVerificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.sportlink.sportlink.utils.RESULT_CODE.*;

@RestController
@RequestMapping("/api/visits")
@AllArgsConstructor
public class VisitController {

    private final VisitService visitService;
    private final LocationService locationService;
    private final VisitTransactionManager visitTransactionManager;
    private final AccountService accountService;

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<DTO_Visit>> getVisitsForUser() {
        Long userId = SecurityUtils.getCurrentAccountId();
        List<DTO_Visit> visits = visitService.getVisitsForUser(userId);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/company")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<List<DTO_Visit>> getVisitsForCompany() {
        Long companyId = SecurityUtils.getCurrentAccountId();
        List<DTO_Visit> visits = visitService.getVisitsForCompany(companyId);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DTO_Visit> getVisitById(@PathVariable Long id) {
        Optional<DTO_Visit> visit = visitService.getVisitById(id);
        return visit.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/open")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RESULT_CODE> openVisit(@RequestBody DTO_VisitRequest request) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        UserAccount acc = null;
        try {
            acc = (UserAccount) accountService.findAccountById(accountId).orElseThrow();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        Optional<DTO_Location> opt = locationService.findLocationById(request.getLocationId());
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        DTO_Location location = opt.get();
        DTO_LocationVerificationRequest verificationRequest = new DTO_LocationVerificationRequest(
                acc.getId(),
                request.getLocationId(),
                request.getUserLatitude(),
                request.getUserLongitude(),
                location.getLatitude(),
                location.getLongitude(),
                request.code
        );

        try {
            RESULT_CODE result = visitTransactionManager.openVisit(acc, verificationRequest);
            switch (result) {
                case VISIT_OPENED -> {
                    return ResponseEntity.ok().body(VISIT_OPENED);
                }
                case LOCATION_NOT_VERIFIED -> {
                    return ResponseEntity.badRequest().body(LOCATION_NOT_VERIFIED);
                }
                case LAST_VISIT_MUST_BE_CLOSED -> {
                    return ResponseEntity.badRequest().body(LAST_VISIT_MUST_BE_CLOSED);
                }
                default -> {
                    return ResponseEntity.internalServerError().body(null);
                }
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/close")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RESULT_CODE> closeVisit(@RequestBody DTO_LocationVerificationRequest request) {
        try {
            Long accountId = SecurityUtils.getCurrentAccountId();
            UserAccount acc = (UserAccount) accountService.findAccountById(accountId).orElseThrow();
            RESULT_CODE result = visitTransactionManager.closeVisit(acc, request);
            switch (result) {
                case VISIT_CLOSED -> {
                    return ResponseEntity.ok().body(VISIT_CLOSED);
                }
                case LOCATION_NOT_VERIFIED -> {
                    return ResponseEntity.badRequest().body(LOCATION_NOT_VERIFIED);
                }
                case LAST_VISIT_MUST_BE_OPEN -> {
                    return ResponseEntity.badRequest().body(LAST_VISIT_MUST_BE_OPEN);
                }
                default -> {
                    return ResponseEntity.internalServerError().body(null);
                }
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
