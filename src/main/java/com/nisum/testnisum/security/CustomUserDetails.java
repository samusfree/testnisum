package com.nisum.testnisum.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserDetails implements org.springframework.security.core.userdetails.UserDetails {
    private UUID id;
    private String email;
    private String password;
    private boolean active;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return active;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return active;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}