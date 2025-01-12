package com.sportlink.sportlink.visit;

import com.sportlink.sportlink.account.I_AccountRepository;
import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.location.I_LocationRepository;
import com.sportlink.sportlink.location.Location;
import com.sportlink.sportlink.location.LocationService;
import com.sportlink.sportlink.reward.I_RewardRepository;
import com.sportlink.sportlink.reward.Reward;
import com.sportlink.sportlink.reward.RewardService;
import com.sportlink.sportlink.transfer.I_TransferRepository;
import com.sportlink.sportlink.transfer.Transfer;
import com.sportlink.sportlink.utils.RESULT_CODE;
import com.sportlink.sportlink.verification.location.DTO_LocationVerificationRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sportlink.sportlink.utils.RESULT_CODE.LOCATION_NOT_VERIFIED;

@Service
@AllArgsConstructor
public class VisitTransactionManager {

    private final VisitService visitService;
    private final I_LocationRepository locationRepository;
    private final LocationService locationService;
    private final RewardService rewardService;
    private final I_TransferRepository i_TransferRepository;
    private final I_AccountRepository i_AccountRepository;
    private final I_RewardRepository i_RewardRepository;

    @Transactional
    public RESULT_CODE openVisit(UserAccount visitor, DTO_LocationVerificationRequest request) {
        try {
            boolean locationVerified = locationService.verifyLocation(request);
            if (!locationVerified) {
                throw new VisitOperationException("Location verification failed", RESULT_CODE.LOCATION_NOT_VERIFIED);
            }

            Location location = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new VisitOperationException("Location not found", LOCATION_NOT_VERIFIED));

            Optional<DTO_Visit> lastVisit = visitService.openVisit(location, visitor, request.getLocationId());
            if (lastVisit.isEmpty()) {
                throw new VisitOperationException("Last visit must be closed before opening a new visit", RESULT_CODE.LAST_VISIT_MUST_BE_CLOSED);
            }

            return RESULT_CODE.VISIT_OPENED;
        } catch (VisitOperationException e) {
            return e.getResultCode();
        } catch (Exception e) {
            throw e; // This will trigger rollback
        }
    }

    @Transactional
    public RESULT_CODE closeVisit(UserAccount visitor, DTO_LocationVerificationRequest request) {
        try {
            boolean locationVerified = locationService.verifyLocation(request);
            if (!locationVerified) {
                throw new VisitOperationException("Location verification failed", RESULT_CODE.LOCATION_NOT_VERIFIED);
            }

            Optional<Visit> closed = visitService.closeVisit(visitor.getId());
            if (closed.isEmpty()) {
                throw new VisitOperationException("Last visit must be open to close it", RESULT_CODE.LAST_VISIT_MUST_BE_OPEN);
            }

            List<Reward> rewards = locationService.getRewardsForLocation(request.getLocationId());
            List<Reward> approved = rewardService.getApprovedRewards(rewards, closed.get());

            List<Transfer> transfers = new ArrayList<>();
            for (Reward reward : approved) {
                visitor.getBalance().merge(reward.getCurrency(), reward.getAmount(), Integer::sum);
                Transfer transfer = new Transfer(null, visitor, LocalDateTime.now(), reward.getCurrency(), reward.getAmount());
                transfers.add(transfer);
            }
            // update rewards
            i_RewardRepository.saveAll(approved);
            // save transfers
            i_TransferRepository.saveAll(transfers);
            // save balance
            i_AccountRepository.save(visitor);

            return RESULT_CODE.VISIT_CLOSED;
        } catch (VisitOperationException e) {
            return e.getResultCode();
        } catch (Exception e) {
            throw e; // This will trigger rollback
        }
    }

}
