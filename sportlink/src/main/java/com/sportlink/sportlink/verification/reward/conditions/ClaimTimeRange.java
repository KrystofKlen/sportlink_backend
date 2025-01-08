package com.sportlink.sportlink.verification.reward.conditions;

import com.sportlink.sportlink.utils.TimeInterval;
import com.sportlink.sportlink.verification.I_VerificationStrategy;
import com.sportlink.sportlink.verification.reward.contexts.IntervalContext;
import lombok.AllArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@AllArgsConstructor
public class ClaimTimeRange implements I_VerificationStrategy {

    IntervalContext intervalContext;

    public boolean verify() {

        if (intervalContext.getIntervals().isEmpty()) {
            return true;
        }

        DayOfWeek dayStart = intervalContext.getTimestampStart().getDayOfWeek();
        DayOfWeek dayStop = intervalContext.getTimestampStop().getDayOfWeek();
        LocalTime timeStart = intervalContext.getTimestampStart().toLocalTime();
        LocalTime timeStop = intervalContext.getTimestampStop().toLocalTime();

        for(TimeInterval interval : intervalContext.getIntervals()) {
            if(isIn(timeStart,timeStop,dayStart,dayStop,interval)) {
                return true;
            }
        }

        return false;
    }

    private boolean isIn(LocalTime timeStart, LocalTime timeStop, DayOfWeek dayStart, DayOfWeek dayStop, TimeInterval timeInterval) {
        return
                timeInterval.getDayStart().equals(dayStart)
                && timeInterval.getStartTime().isBefore(timeStart)
                && timeInterval.getDayEnd().equals(dayStop)
                && timeInterval.getEndTime().isAfter(timeStop);
    }
}
