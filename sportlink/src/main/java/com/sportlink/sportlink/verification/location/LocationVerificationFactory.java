package com.sportlink.sportlink.verification.location;

import com.sportlink.sportlink.codes.CodeData;
import com.sportlink.sportlink.codes.CodesService;
import com.sportlink.sportlink.redis.RedisService;
import com.sportlink.sportlink.utils.PayloadParser;
import com.sportlink.sportlink.verification.I_VerificationStrategy;
import com.sportlink.sportlink.verification.location.contexts.CodeScanContext;
import com.sportlink.sportlink.verification.location.contexts.GeoContext;
import com.sportlink.sportlink.verification.location.contexts.OneTimeCodeContext;
import com.sportlink.sportlink.verification.location.stretegies.OneTimeCode;
import com.sportlink.sportlink.verification.location.stretegies.ScanningCode;
import com.sportlink.sportlink.verification.location.stretegies.UserRadius;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LocationVerificationFactory {

    private final CodesService codesService;
    private final RedisService redisService;

    public LocationVerificationFactory(CodesService codesService, RedisService redisService) {
        this.codesService = codesService;
        this.redisService = redisService;
    }

    public List<I_VerificationStrategy> getVerificationStrategyList(DTO_LocationVerificationRequest dto_location_verification_request, List<LOCATION_VERIFICATION_STRATEGY> strategies) {

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
                    Optional<CodeData> opt = codesService.findByCode(dto_location_verification_request.getCode());
                    if (opt.isEmpty()) {
                        throw new EntityNotFoundException();
                    }
                    CodeScanContext codeScanContext = new CodeScanContext();
                    codeScanContext.setEntityIdExpected(dto_location_verification_request.getLocationId());
                    codeScanContext.setEntityIdScanned(opt.get().getLocationId());
                    verificationStrategies.add(new ScanningCode(codeScanContext));
                }
                case LOCATION_SCANNING_CODE -> {
                    Optional<CodeData> opt = codesService.findByCode(dto_location_verification_request.getCode());
                    if (opt.isEmpty()) {
                        throw new EntityNotFoundException();
                    }
                    CodeScanContext codeScanContext = new CodeScanContext();
                    codeScanContext.setEntityIdExpected(dto_location_verification_request.getUserId());
                    codeScanContext.setEntityIdScanned(opt.get().getUserId());
                    verificationStrategies.add(new ScanningCode(codeScanContext));
                }
                case USER_SCAN_ONETIME_CODE -> {
                    // get data from redis - temp mem
                    String jsonData = redisService.getValue(dto_location_verification_request.getCode());
                    if (jsonData == null) {
                        // code never created or already expired and was removed
                        throw new EntityNotFoundException();
                    }
                    CodeData codeData = PayloadParser.parseJsonToObject(jsonData, CodeData.class);

                    OneTimeCodeContext oneTimeCodeContext = new OneTimeCodeContext();

                    oneTimeCodeContext.setLocationIdInCode(codeData.getLocationId());
                    oneTimeCodeContext.setLocationId(dto_location_verification_request.getLocationId());
                    oneTimeCodeContext.setUserIdInCode(codeData.getUserId());
                    oneTimeCodeContext.setUserId(dto_location_verification_request.getUserId());

                    verificationStrategies.add(new OneTimeCode(oneTimeCodeContext));
                }
            }
        });
        return verificationStrategies;
    }
}
