package com.sportlink.sportlink.verification.location;

import com.sportlink.sportlink.codes.CodesService;
import com.sportlink.sportlink.location.I_LocationRepository;
import com.sportlink.sportlink.location.Location;
import com.sportlink.sportlink.redis.RedisService;
import com.sportlink.sportlink.verification.I_VerificationStrategy;
import com.sportlink.sportlink.verification.location.contexts.CodeScanContext;
import com.sportlink.sportlink.verification.location.contexts.GeoContext;
import com.sportlink.sportlink.verification.location.stretegies.ScanningCode;
import com.sportlink.sportlink.verification.location.stretegies.UserRadius;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class LocationVerificationFactory {

    private final RedisService redisService;
    private final I_LocationRepository locationRepository;

    public LocationVerificationFactory(RedisService redisService, I_LocationRepository locationRepository) {
        this.redisService = redisService;
        this.locationRepository = locationRepository;
    }

    public List<I_VerificationStrategy> getVerificationStrategyList(DTO_LocationVerificationRequest dto_location_verification_request, Set<LOCATION_VERIFICATION_STRATEGY> strategies) {

        List<I_VerificationStrategy> verificationStrategies = new ArrayList<>();

        strategies.stream().forEach(strategy -> {
            switch (strategy) {
                case USER_WITHIN_RADIUS -> {
                    GeoContext geoContext = new GeoContext();
                    geoContext.setLocationLat(dto_location_verification_request.getLocationLatitude());
                    geoContext.setLocationLon(dto_location_verification_request.getLocationLongitude());
                    geoContext.setUserLat(dto_location_verification_request.getUserLatitude());
                    geoContext.setUserLon(dto_location_verification_request.getUserLongitude());
                    geoContext.setMaxRadius(30.0);
                    verificationStrategies.add(new UserRadius(geoContext));
                }
                case USER_SCANNING_CODE -> {
                    Location location = locationRepository.findByCode(dto_location_verification_request.getCode()).orElseThrow();
                    CodeScanContext codeScanContext = new CodeScanContext();
                    codeScanContext.setEntityIdExpected(dto_location_verification_request.getLocationId());
                    codeScanContext.setEntityIdScanned(location.getId());
                    verificationStrategies.add(new ScanningCode(codeScanContext));
                }
                case USER_SCAN_ONETIME_CODE -> {
                    // get data from redis - temp mem
                    String data = redisService.getValue(dto_location_verification_request.getCode());
                    if (data == null) {
                        // code never created or already expired and was removed
                        throw new EntityNotFoundException();
                    }

                    CodeScanContext codeScanContext = new CodeScanContext();
                    codeScanContext.setEntityIdExpected( Long.parseLong(data) );
                    codeScanContext.setEntityIdScanned( dto_location_verification_request.getUserId() );

                    verificationStrategies.add(new ScanningCode(codeScanContext));
                }
            }
        });
        return verificationStrategies;
    }
}
