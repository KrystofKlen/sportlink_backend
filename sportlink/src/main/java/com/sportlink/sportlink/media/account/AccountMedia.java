package com.sportlink.sportlink.media.account;

import com.sportlink.sportlink.account.Account;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class AccountMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "account_id")
    private Account account;

    String profileImgName;
}
