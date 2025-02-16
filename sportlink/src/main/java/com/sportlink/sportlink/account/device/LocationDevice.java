package com.sportlink.sportlink.account.device;

import com.sportlink.sportlink.account.ROLE;
import com.sportlink.sportlink.account.account.Account;
import com.sportlink.sportlink.location.Location;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class LocationDevice extends Account {
    @OneToOne
    Location location;

    public LocationDevice() {
        super();
        setRole(ROLE.ROLE_LOCATION_DEVICE);
    }
}
