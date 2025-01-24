package visit;

import com.sportlink.sportlink.SportlinkApplication;
import com.sportlink.sportlink.account.account.Account;
import com.sportlink.sportlink.account.account.I_AccountRepository;
import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.location.I_LocationRepository;
import com.sportlink.sportlink.location.Location;
import com.sportlink.sportlink.visit.I_VisitRepository;
import com.sportlink.sportlink.visit.Visit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = SportlinkApplication.class)
@Transactional
public class VisitRepositoryIT {

    @Autowired
    I_VisitRepository visitRepository;

    @Autowired
    I_AccountRepository accountRepository;

    @Autowired
    I_LocationRepository locationRepository;

    @Test
    void testFindUsersVisitsToday(){
        Location location = new Location();
        location = locationRepository.save(location);

        UserAccount visitor1 = new UserAccount();
        visitor1 = (UserAccount) accountRepository.save(visitor1);

        UserAccount visitor2 = new UserAccount();
        visitor2 = (UserAccount) accountRepository.save(visitor2);

        Visit visit1 = new Visit();
        visit1.setVisitor(visitor1);
        visit1.setTimestampStop(LocalDateTime.now());
        visit1.setLocation(location);
        visit1 = visitRepository.save(visit1);

        Visit visit2 = new Visit();
        visit2.setVisitor(visitor1);
        visit2.setTimestampStop(LocalDateTime.of(2020, 1, 1, 0, 0));
        visit2.setLocation(location);
        visit2 = visitRepository.save(visit2);

        Visit visit3 = new Visit();
        visit3.setVisitor(visitor2);
        visit3.setTimestampStop(LocalDateTime.now());
        visit3.setLocation(location);
        visit3 = visitRepository.save(visit3);

        Visit visit4 = new Visit();
        visit4.setVisitor(visitor1);
        visit4.setTimestampStop(LocalDateTime.now());
        visit4.setLocation(location);
        visit4 = visitRepository.save(visit4);

        List<Visit> visitsVisitor1 = visitRepository.findVisitsByVisitorToday(visitor1.getId());
        List<Visit> visitsVisitor2 = visitRepository.findVisitsByVisitorToday(visitor2.getId());

        assertTrue(visitsVisitor1.size() == 2);
        assertTrue(visitsVisitor2.size() == 1);

        assertTrue(visitsVisitor1.contains(visit1));
        assertTrue(visitsVisitor1.contains(visit4));
        assertTrue(visitsVisitor2.contains(visit3));
    }

}
