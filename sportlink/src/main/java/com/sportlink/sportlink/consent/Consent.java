package com.sportlink.sportlink.consent;

import com.sportlink.sportlink.account.account.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Consent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @OneToOne
    private Agreement agreement;

    private LocalDateTime consentGivenAt;
    private LocalDateTime consentExpiredAt;
}
