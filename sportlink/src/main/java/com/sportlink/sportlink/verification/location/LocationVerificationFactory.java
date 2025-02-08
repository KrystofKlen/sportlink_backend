package com.sportlink.sportlink.verification.location;

import com.sportlink.sportlink.location.I_LocationRepository;
import com.sportlink.sportlink.location.Location;
import com.sportlink.sportlink.redis.RedisService;
import com.sportlink.sportlink.verification.I_VerificationStrategy;
import com.sportlink.sportlink.verification.location.contexts.CodeScanContext;
import com.sportlink.sportlink.verification.location.contexts.GeoContext;
import com.sportlink.sportlink.verification.location.contexts.VisitsLimitContext;
import com.sportlink.sportlink.verification.location.stretegies.ScanningCode;
import com.sportlink.sportlink.verification.location.stretegies.UserRadius;
import com.sportlink.sportlink.verification.location.stretegies.VisitsLimit;
import com.sportlink.sportlink.visit.I_VisitRepository;
import com.sportlink.sportlink.visit.Visit;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class LocationVerificationFactory {

    private final RedisService redisService;
    private final I_LocationRepository locationRepository;
    private final I_VisitRepository visitRepository;

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

                    String[] parts = data.split("-");
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Invalid data format in Redis: " + data);
                    }

                    long expectedUserId = Long.parseLong(parts[0]);
                    long expectedLocationId = Long.parseLong(parts[1]);

                    CodeScanContext cmpUserId = new CodeScanContext();
                    cmpUserId.setEntityIdExpected( expectedUserId );
                    cmpUserId.setEntityIdScanned( dto_location_verification_request.getUserId() );

                    CodeScanContext cmpLocationId = new CodeScanContext();
                    cmpLocationId.setEntityIdExpected( expectedLocationId );
                    cmpLocationId.setEntityIdScanned( expectedLocationId );

                    verificationStrategies.add(new ScanningCode(cmpUserId));
                    verificationStrategies.add(new ScanningCode(cmpLocationId));
                }
                case ONE_VISIT_PER_DAY -> {
                    List<Visit> visits = visitRepository.findVisitsByVisitorToday(dto_location_verification_request.getUserId());
                    VisitsLimitContext context = new VisitsLimitContext();
                    context.setLocationId(dto_location_verification_request.getLocationId());
                    context.setVisitsToday(visits);
                    context.setLimitPerDay(1);

                    verificationStrategies.add(new VisitsLimit(context));
                }
            }
        });
        return verificationStrategies;
    }
}
