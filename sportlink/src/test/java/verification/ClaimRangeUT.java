package verification;

import com.sportlink.sportlink.utils.TimeInterval;
import com.sportlink.sportlink.verification.reward.conditions.ClaimTimeRange;
import com.sportlink.sportlink.verification.reward.contexts.IntervalContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClaimRangeUT {
    private IntervalContext intervalContext;
    private ClaimTimeRange claimTimeRange;

    @BeforeEach
    void setUp() {
        intervalContext = mock(IntervalContext.class);
        claimTimeRange = new ClaimTimeRange(intervalContext);
    }

    @Test
    void verify_ShouldReturnTrue_WhenIntervalsAreEmpty() {
        when(intervalContext.getIntervals()).thenReturn(Collections.emptyList());

        boolean result = claimTimeRange.verify();

        assertTrue(result, "Verification should return true when no intervals are defined.");
        verify(intervalContext, times(1)).getIntervals();
    }

    @Test
    void verify_ShouldReturnTrue_WhenTimeIsWithinInterval() {
        TimeInterval interval = new TimeInterval(
                DayOfWeek.MONDAY, LocalTime.of(9, 0),
                DayOfWeek.MONDAY, LocalTime.of(17, 0)
        );

        when(intervalContext.getIntervals()).thenReturn(Arrays.asList(interval));
        when(intervalContext.getTimestampStart()).thenReturn(LocalDateTime.of(2024, 6, 3, 10, 0)); // Monday 10:00 AM
        when(intervalContext.getTimestampStop()).thenReturn(LocalDateTime.of(2024, 6, 3, 16, 0)); // Monday 4:00 PM

        boolean result = claimTimeRange.verify();

        assertTrue(result, "Verification should return true when time is within the interval.");
    }

    @Test
    void verify_ShouldReturnFalse_WhenTimeIsOutsideInterval() {
        TimeInterval interval = new TimeInterval(
                DayOfWeek.MONDAY, LocalTime.of(9, 0),
                DayOfWeek.MONDAY, LocalTime.of(17, 0)
        );

        when(intervalContext.getIntervals()).thenReturn(Arrays.asList(interval));
        when(intervalContext.getTimestampStart()).thenReturn(LocalDateTime.of(2024, 6, 3, 8, 0)); // Monday 8:00 AM
        when(intervalContext.getTimestampStop()).thenReturn(LocalDateTime.of(2024, 6, 3, 18, 0)); // Monday 6:00 PM

        boolean result = claimTimeRange.verify();

        assertFalse(result, "Verification should return false when time is outside the interval.");
    }

    @Test
    void verify_ShouldReturnTrue_WhenMultipleIntervalsAndOneMatches() {
        TimeInterval interval1 = new TimeInterval(
                DayOfWeek.MONDAY, LocalTime.of(9, 0),
                DayOfWeek.MONDAY, LocalTime.of(11, 0)
        );

        TimeInterval interval2 = new TimeInterval(
                DayOfWeek.TUESDAY, LocalTime.of(14, 0),
                DayOfWeek.TUESDAY, LocalTime.of(16, 0)
        );

        when(intervalContext.getIntervals()).thenReturn(Arrays.asList(interval1, interval2));
        when(intervalContext.getTimestampStart()).thenReturn(LocalDateTime.of(2024, 6, 4, 14, 30)); // Tuesday 2:30 PM
        when(intervalContext.getTimestampStop()).thenReturn(LocalDateTime.of(2024, 6, 4, 15, 30)); // Tuesday 3:30 PM

        boolean result = claimTimeRange.verify();

        assertTrue(result, "Verification should return true when at least one interval matches.");
    }

    @Test
    void verify_ShouldReturnFalse_WhenNoIntervalsMatch() {
        TimeInterval interval1 = new TimeInterval(
                DayOfWeek.MONDAY, LocalTime.of(9, 0),
                DayOfWeek.MONDAY, LocalTime.of(11, 0)
        );

        TimeInterval interval2 = new TimeInterval(
                DayOfWeek.TUESDAY, LocalTime.of(14, 0),
                DayOfWeek.TUESDAY, LocalTime.of(16, 0)
        );

        when(intervalContext.getIntervals()).thenReturn(Arrays.asList(interval1, interval2));
        when(intervalContext.getTimestampStart()).thenReturn(LocalDateTime.of(2024, 6, 4, 17, 0)); // Tuesday 5:00 PM
        when(intervalContext.getTimestampStop()).thenReturn(LocalDateTime.of(2024, 6, 4, 18, 0)); // Tuesday 6:00 PM

        boolean result = claimTimeRange.verify();

        assertFalse(result, "Verification should return false when no intervals match.");
    }

    @Test
    void verify_ShouldReturnTrue_WhenTimeSpansAcrossDifferentDaysAndIsWithinInterval() {
        TimeInterval interval = new TimeInterval(
                DayOfWeek.MONDAY, LocalTime.of(22, 0),  // Monday 10:00 PM
                DayOfWeek.TUESDAY, LocalTime.of(6, 0)  // Tuesday 6:00 AM
        );

        when(intervalContext.getIntervals()).thenReturn(Arrays.asList(interval));
        when(intervalContext.getTimestampStart()).thenReturn(LocalDateTime.of(2024, 6, 3, 23, 0)); // Monday 11:00 PM
        when(intervalContext.getTimestampStop()).thenReturn(LocalDateTime.of(2024, 6, 4, 1, 0));   // Tuesday 1:00 AM

        boolean result = claimTimeRange.verify();

        assertTrue(result, "Verification should return true when time spans across different days and is within the interval.");
    }

    @Test
    void verify_ShouldReturnFalse_WhenTimeSpansAcrossDifferentDaysButIsOutsideInterval() {
        TimeInterval interval = new TimeInterval(
                DayOfWeek.MONDAY, LocalTime.of(22, 0),  // Monday 10:00 PM
                DayOfWeek.TUESDAY, LocalTime.of(6, 0)  // Tuesday 6:00 AM
        );

        when(intervalContext.getIntervals()).thenReturn(Arrays.asList(interval));
        when(intervalContext.getTimestampStart()).thenReturn(LocalDateTime.of(2024, 6, 3, 20, 0)); // Monday 8:00 PM
        when(intervalContext.getTimestampStop()).thenReturn(LocalDateTime.of(2024, 6, 3, 21, 0));   // Monday 9:00 PM

        boolean result = claimTimeRange.verify();

        assertFalse(result, "Verification should return false when time spans across different days but is outside the interval.");
    }

    @Test
    void verify_ShouldReturnTrue_WhenMultipleIntervalsAndOneSpansDifferentDaysAndMatches() {
        TimeInterval interval1 = new TimeInterval(
                DayOfWeek.MONDAY, LocalTime.of(9, 0),
                DayOfWeek.MONDAY, LocalTime.of(17, 0)
        );

        TimeInterval interval2 = new TimeInterval(
                DayOfWeek.FRIDAY, LocalTime.of(23, 0),
                DayOfWeek.SATURDAY, LocalTime.of(5, 0)
        );

        when(intervalContext.getIntervals()).thenReturn(Arrays.asList(interval1, interval2));
        when(intervalContext.getTimestampStart()).thenReturn(LocalDateTime.of(2024, 6, 7, 23, 30)); // Friday 11:30 PM
        when(intervalContext.getTimestampStop()).thenReturn(LocalDateTime.of(2024, 6, 8, 3, 0));    // Saturday 3:00 AM

        boolean result = claimTimeRange.verify();

        assertTrue(result, "Verification should return true when one interval spans different days and matches.");
    }

    @Test
    void verify_ShouldReturnFalse_WhenTimeSpansAcrossDifferentDaysAndDoesNotMatchAnyInterval() {
        TimeInterval interval1 = new TimeInterval(
                DayOfWeek.MONDAY, LocalTime.of(9, 0),
                DayOfWeek.MONDAY, LocalTime.of(17, 0)
        );

        TimeInterval interval2 = new TimeInterval(
                DayOfWeek.FRIDAY, LocalTime.of(23, 0),
                DayOfWeek.SATURDAY, LocalTime.of(5, 0)
        );

        when(intervalContext.getIntervals()).thenReturn(Arrays.asList(interval1, interval2));
        when(intervalContext.getTimestampStart()).thenReturn(LocalDateTime.of(2024, 6, 7, 21, 0)); // Friday 9:00 PM
        when(intervalContext.getTimestampStop()).thenReturn(LocalDateTime.of(2024, 6, 8, 6, 0));   // Saturday 6:00 AM

        boolean result = claimTimeRange.verify();

        assertFalse(result, "Verification should return false when no interval matches, even if times span across different days.");
    }
}
