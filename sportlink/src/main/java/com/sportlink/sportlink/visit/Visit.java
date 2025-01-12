package com.sportlink.sportlink.visit;

import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.location.Location;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long visitId;

    @OneToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    private LocalDateTime timestampStart;
    private LocalDateTime timestampStop;

    @Enumerated(EnumType.STRING)
    private VisitState state;

    @ManyToOne
    private UserAccount visitor;
}
