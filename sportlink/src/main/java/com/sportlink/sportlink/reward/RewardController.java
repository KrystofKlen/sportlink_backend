package com.sportlink.sportlink.reward;

import com.sportlink.sportlink.utils.DTO_Adapter;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rewards")
@AllArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    // Create a new reward
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','COMPANY')")
    public ResponseEntity<DTO_Reward> createReward(@Valid @RequestBody DTO_Reward dto) {
        try {
            DTO_Reward createdReward = rewardService.save(dto);
            return new ResponseEntity<>(createdReward, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Update an existing reward
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN','COMPANY')")
    public ResponseEntity<DTO_Reward> updateReward(@Valid @RequestBody DTO_Reward dto) {
        try {
            DTO_Reward updatedReward = rewardService.update(dto);
            return ResponseEntity.ok(updatedReward);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
