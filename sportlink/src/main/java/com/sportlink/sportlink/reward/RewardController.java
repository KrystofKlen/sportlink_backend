package com.sportlink.sportlink.reward;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rewards")
public class RewardController {

    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    // Create a new reward
    @PostMapping
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
    public ResponseEntity<DTO_Reward> updateReward(@Valid @RequestBody DTO_Reward dto) {
        try {
            DTO_Reward updatedReward = rewardService.update(dto);
            return ResponseEntity.ok(updatedReward);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Get rewards for a specific location
    @GetMapping("/for-location/{locationId}")
    public ResponseEntity<List<DTO_Reward>> getRewardsForLocation(@PathVariable Long locationId) {
        try {
            List<DTO_Reward> rewards = rewardService.getRewardsForLocation(locationId);
            return ResponseEntity.ok(rewards);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Add a new reward for a location
    @PostMapping("/location/{locationId}")
    public ResponseEntity<List<DTO_Reward>> addNewRewardForLocation(@PathVariable Long locationId, @Valid @RequestBody DTO_Reward dto) {
        try {
            List<DTO_Reward> updatedRewards = rewardService.addNewRewardForLocation(dto, locationId);
            return ResponseEntity.ok(updatedRewards);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
