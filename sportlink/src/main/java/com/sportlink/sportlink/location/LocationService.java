package com.sportlink.sportlink.location;

import com.sportlink.sportlink.account.account.AccountService;
import com.sportlink.sportlink.account.company.CompanyAccount;
import com.sportlink.sportlink.currency.Currency;
import com.sportlink.sportlink.currency.I_CurrencyRepository;
import com.sportlink.sportlink.reward.DTO_Reward;
import com.sportlink.sportlink.reward.Reward;
import com.sportlink.sportlink.security.EncryptionUtil;
import com.sportlink.sportlink.utils.DTO_Adapter;
import com.sportlink.sportlink.utils.ImgService;
import com.sportlink.sportlink.verification.I_VerificationStrategy;
import com.sportlink.sportlink.verification.location.DTO_LocationVerificationRequest;
import com.sportlink.sportlink.verification.location.LocationVerificationFactory;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class LocationService {

    private final I_LocationRepository locationRepository;
    private final I_CurrencyRepository currencyRepository;
    private final DTO_Adapter adapter;
    private final LocationVerificationFactory verificationFactory;
    public static final double MAX_DEVIATION = 0.5;
    private final AccountService accountService;
    private final ImgService imgService;

    @Transactional
    public DTO_Location saveLocation(@Valid DTO_Location dtoLocation, Long issuerId) {

        CompanyAccount issuer = (CompanyAccount) accountService.findAccountById(issuerId).orElseThrow();

        if (dtoLocation.getVerificationStrategies().isEmpty()) {
            throw new IllegalArgumentException("No verification strategy found");
        }
        Location location = adapter.getLocationFromDTO(dtoLocation);
        location.setIssuer(issuer);
        Location savedLocation = locationRepository.save(location);
        DTO_Location dto = adapter.getDTO_Location(savedLocation);
        log.info("Saved location: " + dto);
        return dto;
    }

    // This method will update only those variable which are != null
    @Transactional
    public DTO_Location updateLocation(DTO_Location dtoLocation) {

        Optional<Location> existingLocationOpt = locationRepository.findById(dtoLocation.getId());
        if (existingLocationOpt.isEmpty()) {
            throw new RuntimeException("Location not found with id: " + dtoLocation.getId());
        }

        Location existingLocation = existingLocationOpt.get();

        if (dtoLocation.getName() != null) {
            existingLocation.setName(dtoLocation.getName());
        }
        if (dtoLocation.getAddress() != null) {
            existingLocation.setAddress(dtoLocation.getAddress());
        }
        if (dtoLocation.getDescription() != null) {
            existingLocation.setDescription(dtoLocation.getDescription());
        }
        if (dtoLocation.getActivities() != null && !dtoLocation.getActivities().isEmpty()) {
            existingLocation.setActivities(new HashSet<>(dtoLocation.getActivities()));
        }
        if (dtoLocation.getLongitude() != null) {
            existingLocation.setLongitude(dtoLocation.getLongitude());
        }
        if (dtoLocation.getLatitude() != null) {
            existingLocation.setLatitude(dtoLocation.getLatitude());
        }
        if (dtoLocation.getVerificationStrategies() != null && !dtoLocation.getVerificationStrategies().isEmpty()) {
            existingLocation.setVerificationStrategies(new HashSet<>(dtoLocation.getVerificationStrategies()));
        }
        if (dtoLocation.getImagesUUIDs() != null && !dtoLocation.getImagesUUIDs().isEmpty()) {
            existingLocation.setImagesUUID(dtoLocation.getImagesUUIDs());
        }

        // 4. Save the updated entity
        Location updatedLocation = locationRepository.save(existingLocation);
        DTO_Location dto = adapter.getDTO_Location(updatedLocation);
        log.info("Updated location: " + dto);
        return dto;
    }

    @Transactional
    public void deleteLocation(Long id) {
        locationRepository.deleteById(id);
    }

    public Optional<DTO_Location> findLocationById(Long id) {
        Optional<Location> locationOpt = locationRepository.findById(id);
        return locationOpt.map(location -> adapter.getDTO_Location(location));
    }

    public List<DTO_Location> findNearbyLocations(double lon, double lat, int radiusMeter) {
        List<Location> nearby = locationRepository.findNearbyLocations(lon, lat, MAX_DEVIATION);

        return nearby.stream().filter(location ->
                isWithinRadius(
                        location.getLongitude(),
                        location.getLatitude(),
                        lon,
                        lat,
                        radiusMeter)
        ).map(adapter::getDTO_Location).collect(Collectors.toList());

    }

    public Set<DTO_Location> findByActivities(List<ACTIVITY> activities) {
        Set<DTO_Location> result = new HashSet<>();
        activities.forEach(activity -> {
            locationRepository.findByActivity(activity)
                    .stream().map(adapter::getDTO_Location).forEach(result::add);
        });
        return result;
    }

    public boolean verifyLocation(DTO_LocationVerificationRequest request) {
        Location location = locationRepository.findById(request.getLocationId()).orElseThrow();
        List<I_VerificationStrategy> strategies =
                verificationFactory.getVerificationStrategyList(request, location.getVerificationStrategies());
        for (I_VerificationStrategy strategy : strategies) {
            if (!strategy.verify()) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    public DTO_Location addReward(long locationId, DTO_Reward dto) {
        Location location = locationRepository.findById(locationId).orElseThrow();
        Currency currency = currencyRepository.findCurrencyByName(dto.getCurrency()).orElseThrow();

        Reward reward = new Reward();
        reward.setRewardConditions(dto.getRewardConditions());
        reward.setCurrency(currency);
        reward.setAmount(dto.getAmount());
        reward.setTotalClaimsLimit(dto.getTotalClaimsLimit());
        reward.setTotalClaimsCount(0);
        reward.setMonthClaimsLimit(dto.getMonthClaimsLimit());
        reward.setMonthClaimsCount(0);
        reward.setIntervals(dto.getIntervals());
        reward.setTotalClaimsCount(dto.getTotalClaimsCount());

        location.getRewards().add(reward);
        Location saved = locationRepository.save(location);
        log.info("New reward added: " + dto + " for Location: " + locationId);
        return adapter.getDTO_Location(saved);
    }

    @Transactional
    public void deleteReward(long locationId, long rewardId) {
        Location location = locationRepository.findById(locationId).orElseThrow();
        Reward reward = location.getRewards()
                .stream().filter(r -> r.getId() == rewardId).findFirst().orElseThrow();
        location.getRewards().remove(reward);
        locationRepository.save(location);
        log.info("Reward deleted: " + reward);
    }

    public boolean allowModification(long accountId, long locationId) {
        return locationRepository.findById(locationId).orElseThrow().getIssuer().getId() == accountId;
    }

    public static boolean isWithinRadius(double lon1, double lat1, double lon2, double lat2, int radius) {
        // Convert coordinates from degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Calculate differences
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Apply Haversine formula
        double a = Math.pow(Math.sin(deltaLat / 2), 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.pow(Math.sin(deltaLon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calculate distance
        double distance = 6371000 * c;

        // Check if distance is within the radius
        return distance <= radius;
    }

    // Returns all locations which are owned/issued by company with companyId
    public List<DTO_Location> findByIssuerId(Long companyId) {
        return locationRepository.findByIssuerId(companyId).stream().map(adapter::getDTO_Location).toList();
    }

    public boolean uploadImg(long locationId, MultipartFile image) {
        String filename = UUID.randomUUID().toString() + "jpg";
        boolean saved = imgService.saveImage(imgService.PATH_LOCATION, filename, image);
        if (!saved) {
            return false;
        }
        Location location = locationRepository.findById(locationId).orElseThrow();
        location.getImagesUUID().add(filename);
        locationRepository.save(location);
        return true;
    }

    public boolean deleteImg(long locationId, String filename) {
        boolean deleted = imgService.deleteImage(imgService.PATH_LOCATION, filename);
        if (!deleted) {
            return false;
        }
        Location location = locationRepository.findById(locationId).orElseThrow();
        location.getImagesUUID().remove(filename);
        locationRepository.save(location);
        return true;
    }

    public List<Reward> getRewardsForLocation(Long locationId) {
        return locationRepository.getRewardsForLocation(locationId);
    }

    @Transactional
    public String refreshCode(Long locationId, Long accountRequestingId) {
        Location location = locationRepository.findById(locationId).orElseThrow();
        if(location.getIssuer().getId() != accountRequestingId){
            throw new RuntimeException();
        }
        location.setCode(EncryptionUtil.generateRandomSequence(10));
        location = locationRepository.save(location);
        return location.getCode();
    }
}
