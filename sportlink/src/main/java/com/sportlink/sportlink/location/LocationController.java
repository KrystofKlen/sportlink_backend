package com.sportlink.sportlink.location;

import com.sportlink.sportlink.account.company.CompanyAccount;
import com.sportlink.sportlink.reward.DTO_Reward;
import com.sportlink.sportlink.reward.Reward;
import com.sportlink.sportlink.security.SecurityUtils;
import com.sportlink.sportlink.utils.DTO_Adapter;
import com.sportlink.sportlink.utils.ImgService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/locations")
public class LocationController {

    private final LocationService locationService;
    private final DTO_Adapter adapter;
    private final ImgService imgService;

    /**
     * Create a new location
     */
    @PostMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<DTO_Location> createLocation(@Valid @RequestBody DTO_Location dtoLocation) {
        Long issuerId = SecurityUtils.getCurrentAccountId();
        DTO_Location savedLocation = locationService.saveLocation(dtoLocation, issuerId);
        return ResponseEntity.ok(savedLocation);
    }

    /**
     * Update an existing location
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','COMPANY')")
    public ResponseEntity<DTO_Location> updateLocation(@RequestBody DTO_Location dtoLocation) {
        // get account id from request
        // get account id from request
        Long accountId = 1L;
        boolean isAllowed = locationService.allowModification(accountId, dtoLocation.getId());
        if (!isAllowed) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        DTO_Location updatedLocation = locationService.updateLocation(dtoLocation);
        return ResponseEntity.ok(updatedLocation);
    }

    /**
     * Delete a location by ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','COMPANY')")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        boolean isAllowed = locationService.allowModification(accountId, id);
        if (!isAllowed) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{locationId}/reward")
    @PreAuthorize("hasAnyRole('ADMIN','COMPANY')")
    public ResponseEntity<DTO_Location> addRewardLocation(@Valid @RequestBody DTO_Reward reward, @PathVariable long locationId) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        boolean isAllowed = locationService.allowModification(accountId, locationId);
        if (!isAllowed) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        DTO_Location result = locationService.addReward(locationId, reward);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{locationId}/reward")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<DTO_Reward>> getRewardsForLocation(@PathVariable Long locationId) {
        try {
            List<Reward> rewards = locationService.getRewardsForLocation(locationId);
            return ResponseEntity.ok(rewards.stream().map(adapter::getDTO_Reward).toList());
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{locationId}/reward/{rewardId}")
    @PreAuthorize("hasAnyRole('ADMIN','COMPANY')")
    public ResponseEntity<Void> deleteRewardLocation(@PathVariable Long locationId, @PathVariable Long rewardId) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        boolean isAllowed = locationService.allowModification(accountId, rewardId);
        if (!isAllowed) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        locationService.deleteReward(locationId, rewardId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Find a location by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<DTO_Location> getLocationById(@PathVariable Long id) {
        Optional<DTO_Location> location = locationService.findLocationById(id);
        return location.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Finds locations where at least 1 activity is in activities
     *
     * @param activities
     * @return
     */
    @GetMapping("/activities")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<DTO_Location>> getLocationsWithActivities(@RequestBody List<ACTIVITY> activities) {
        Set<DTO_Location> result = locationService.findByActivities(activities);
        return ResponseEntity.ok(result.stream().toList());
    }

    /**
     * Find nearby locations based on longitude, latitude, and radius
     */
    @GetMapping("/nearby")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<DTO_Location>> getNearbyLocations(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @Positive @RequestParam int radius) {

        List<DTO_Location> locations = locationService.findNearbyLocations(longitude, latitude, radius);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/for-company/{companyId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<DTO_Location>> getLocationsForCompany(@PathVariable Long companyId) {
        List<DTO_Location> result = locationService.findByIssuerId(companyId);
        return ResponseEntity.ok(result);
    }

    // Endpoint to upload an image
    @PostMapping("/{locationId}/images")
    @PreAuthorize("hasAnyRole('ADMIN','COMPANY')")
    public ResponseEntity<String> uploadImage(@PathVariable long locationId, @RequestParam("image") MultipartFile image) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        boolean isAllowed = locationService.allowModification(accountId, locationId);
        if (!isAllowed) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean success = locationService.uploadImg(locationId, image);
        if (success) {
            return ResponseEntity.ok("Image uploaded successfully.");
        } else {
            return ResponseEntity.status(500).body("Failed to upload image.");
        }
    }

    @GetMapping("/images/{imgName}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Resource> getImage(@PathVariable String imgName) {
        Optional<Resource> image = imgService.getImage(imgService.PATH_LOCATION, imgName);
        if(image.isPresent()) {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image.get());
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    // Endpoint to delete an image
    @DeleteMapping("/{locationId}/images/{filename}")
    @PreAuthorize("hasAnyRole('ADMIN','COMPANY')")
    public ResponseEntity<String> deleteImage(@PathVariable long locationId, @PathVariable String filename) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        boolean isAllowed = locationService.allowModification(accountId, locationId);
        if (!isAllowed) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean success = locationService.deleteImg(locationId, filename);
        if (success) {
            return ResponseEntity.ok("Image deleted successfully.");
        } else {
            return ResponseEntity.status(500).body("Failed to delete image.");
        }
    }

    @PatchMapping("/refresh-code/{locationId}")
    @PreAuthorize("hasAnyRole('ADMIN','COMPANY')")
    public ResponseEntity<String> refreshLocationCode(@PathVariable Long locationId) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        String newCode = locationService.refreshCode(accountId, locationId);
        return ResponseEntity.ok(newCode);
    }
}
