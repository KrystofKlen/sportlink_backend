package com.sportlink.sportlink.verification.location.contexts;

import com.sportlink.sportlink.visit.Visit;
import lombok.Data;

import java.util.List;

@Data
public class VisitsLimitContext {
    List<Visit> visitsToday;
    Long locationId;
    int limitPerDay;
}
