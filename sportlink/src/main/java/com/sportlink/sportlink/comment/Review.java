package com.sportlink.sportlink.comment;

import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.location.Location;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    UserAccount userPosting;

    private String content;

    @ManyToOne
    private Location location;
}
