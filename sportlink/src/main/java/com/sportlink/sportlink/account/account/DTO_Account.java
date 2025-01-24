package com.sportlink.sportlink.account.account;

import com.sportlink.sportlink.account.ROLE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTO_Account implements UserDetails {
    private Long id;

    private String loginEmail;

    private String username;

    private String password;

    private ROLE role;

    private String profilePicUUID;

    private ACCOUNT_STATUS status;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert role to a list of GrantedAuthority
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Assuming all accounts do not expire
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Account is locked only if the status is BANNED
        return this.status != ACCOUNT_STATUS.BANNED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Assuming credentials do not expire
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Account is enabled if the status is ACTIVE
        return this.status == ACCOUNT_STATUS.ACTIVE;
    }
}
