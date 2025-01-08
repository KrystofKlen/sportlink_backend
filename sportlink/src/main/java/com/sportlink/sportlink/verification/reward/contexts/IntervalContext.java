package com.sportlink.sportlink.verification.reward.contexts;

import com.sportlink.sportlink.utils.TimeInterval;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class IntervalContext {
    private List<TimeInterval> intervals;
    private LocalDateTime timestampStart;
    private LocalDateTime timestampStop;
}
