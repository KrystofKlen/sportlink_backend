package com.sportlink.sportlink.visit;

import com.sportlink.sportlink.location.DTO_Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTO_Visit {

    private long visitId;

    private DTO_Location location;

    private LocalDateTime timestampStart;
    private LocalDateTime timestampStop;

    VisitState visitState;
}
