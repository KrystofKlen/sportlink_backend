package com.sportlink.sportlink.transfer;

import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.currency.Currency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private UserAccount receiver;

    private LocalDateTime timestamp;

    @ManyToOne
    private Currency currency;

    private Integer amount;
}
