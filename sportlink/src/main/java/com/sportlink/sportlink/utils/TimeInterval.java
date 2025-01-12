package com.sportlink.sportlink.utils;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeInterval {
    DayOfWeek dayStart;
    LocalTime startTime;
    DayOfWeek dayEnd;
    LocalTime endTime;
}
