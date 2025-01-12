package com.sportlink.sportlink.visit;

import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.location.Location;
import com.sportlink.sportlink.utils.DTO_Adapter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class VisitService {

    private final I_VisitRepository visitRepository;
    private final DTO_Adapter adapter;

    @Transactional
    public DTO_Visit saveVisit(DTO_Visit dto_visit, UserAccount userAccount) {

        Visit visit = new Visit(
                dto_visit.getVisitId(),
                adapter.getLocationFromDTO(dto_visit.getLocation()),
                dto_visit.getTimestampStart(),
                dto_visit.getTimestampStop(),
                dto_visit.getVisitState(),
                userAccount
        );

        return adapter.getDTO_Visit(visitRepository.save(visit));
    }

    public List<DTO_Visit> getVisitsForUser(Long userId) {
        return visitRepository.getVisitsForUser(userId).stream().map(adapter::getDTO_Visit).toList();
    }

    public List<DTO_Visit> getVisitsForCompany(Long companyId) {
        return visitRepository.getVisitsForCompany(companyId).stream().map(adapter::getDTO_Visit).toList();
    }

    public Optional<DTO_Visit> getVisitById(Long id) {
        Optional<Visit> optionalVisit = visitRepository.findById(id);
        return optionalVisit.map(adapter::getDTO_Visit);
    }

    // requires verified location
    @Transactional
    public Optional<DTO_Visit> openVisit(Location location, UserAccount visitor, Long userId) {

        Optional<Visit> last = visitRepository.getLastByVisitorId(userId);
        if (last.isPresent() && last.get().getState().equals(VisitState.OPEN)) {
            // last is still open -> can not open new one -> first need to finish the previous
            return Optional.empty();
        }
        // create Visit
        Visit visit = new Visit(null, location, LocalDateTime.now(), null, VisitState.OPEN, visitor);
        visit = visitRepository.save(visit);
        return Optional.of(adapter.getDTO_Visit(visit));
    }

    // requires verified location
    @Transactional
    public Optional<Visit> closeVisit(Long userId) {

        Visit last = visitRepository.getLastByVisitorId(userId).orElseThrow();
        if (last.getState().equals(VisitState.CLOSED)) {
            // last visit needs to be opened
            return Optional.empty();
        }
        // finalize visit
        last.setTimestampStop(LocalDateTime.now());
        last.setState(VisitState.CLOSED);
        return Optional.of(visitRepository.save(last));
    }
}
