package com.sportlink.sportlink.transfer;

import com.sportlink.sportlink.account.Account;
import com.sportlink.sportlink.account.UserAccount;
import com.sportlink.sportlink.balance.Balance;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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

    @OneToOne(cascade = CascadeType.ALL)
    private Balance balanceTransfered;
}
