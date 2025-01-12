package visit;

import com.sportlink.sportlink.SportlinkApplication;
import com.sportlink.sportlink.account.AccountService;
import com.sportlink.sportlink.account.I_AccountRepository;
import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.location.I_LocationRepository;
import com.sportlink.sportlink.location.Location;
import com.sportlink.sportlink.utils.DTO_Adapter;
import com.sportlink.sportlink.visit.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SportlinkApplication.class)
@Transactional
class VisitServiceIT {

    @Autowired
    private I_VisitRepository visitRepository;

    @Autowired
    private DTO_Adapter adapter;

    @Autowired
    private VisitService visitService;

    private UserAccount visitor;
    private Location location;
    @Autowired
    private I_LocationRepository i_LocationRepository;
    @Autowired
    private I_AccountRepository i_AccountRepository;
    @Autowired
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        i_AccountRepository.deleteAll();
        i_LocationRepository.deleteAll();
        visitRepository.deleteAll();

        visitor = new UserAccount(); // Configure as needed
        visitor.setUsername("visitor");
        visitor = i_AccountRepository.save(visitor);
        location = new Location();  // Configure as needed
        location.setLatitude(40.8);
        i_LocationRepository.save(location);
    }

    @Test
    void testOpenVisitWhenNoOpenVisitExists() {

        Optional<DTO_Visit> result = visitService.openVisit(location, visitor, visitor.getId());

        assertTrue(result.isPresent());
        assertEquals(1, visitRepository.count());

        Visit savedVisit = visitRepository.findAll().get(0);
        assertEquals(VisitState.OPEN, savedVisit.getState());
        assertEquals(visitor.getUsername(), savedVisit.getVisitor().getUsername());
        assertEquals( 40.8 , savedVisit.getLocation().getLatitude());
        assertNull(savedVisit.getTimestampStop());
        assertTrue(LocalDateTime.now().isAfter( savedVisit.getTimestampStart() ));
    }

    @Test
    void testOpenVisitWhenOpenVisitExists() {
        Optional<DTO_Visit> result1 = visitService.openVisit(location, visitor, visitor.getId());
        assertTrue(result1.isPresent());

        Optional<DTO_Visit> result2 = visitService.openVisit(location, visitor, visitor.getId());

        assertFalse(result2.isPresent());
        assertEquals(1, visitRepository.count());
        Visit savedVisit = visitRepository.findAll().get(0);
        assertEquals(VisitState.OPEN, savedVisit.getState());
        assertEquals(visitor.getUsername(), savedVisit.getVisitor().getUsername());
    }

    @Test
    void testCloseVisitWhenOpenVisitExists() {
        Long userId = 1L;
        Visit openVisit = new Visit(null, location, LocalDateTime.now(), null, VisitState.OPEN, visitor);
        visitRepository.save(openVisit);

        Optional<Visit> result = visitService.closeVisit(visitor.getId());

        assertTrue(result.isPresent());
        Visit closedVisit = result.get();
        assertEquals(VisitState.CLOSED, closedVisit.getState());
        assertNotNull(closedVisit.getTimestampStop());

        Visit savedVisit = visitRepository.findById(closedVisit.getVisitId()).orElseThrow();
        assertEquals(VisitState.CLOSED, savedVisit.getState());
        assertTrue(savedVisit.getTimestampStop().isAfter( savedVisit.getTimestampStart() ));
    }

    @Test
    void testCloseVisitWhenNoOpenVisitExists() {
        Long userId = 1L;
        Visit closedVisit = new Visit(null, location, LocalDateTime.now(), LocalDateTime.now(), VisitState.CLOSED, visitor);
        visitRepository.save(closedVisit);

        Optional<Visit> result = visitService.closeVisit(visitor.getId());

        assertFalse(result.isPresent());
    }

    @Test
    void testCloseVisitWhenNoVisitExists() {
        Long userId = 1L;

        assertThrows(NoSuchElementException.class, () -> visitService.closeVisit(visitor.getId()));
    }
}
