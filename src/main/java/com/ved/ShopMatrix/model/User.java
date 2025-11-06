package com.ved.ShopMatrix.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Document(collection = "users")
public class User implements UserDetails {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    @JsonIgnore
    private String password;

    private String firstName;
    private String lastName;

    private Cart cart = new Cart();

    // Use the Role enum
    private Set<Role> roles = new HashSet<>();

    private Instant createdAt = Instant.now();

    // UserDetails fields
    private boolean enabled = true;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;

    // --- UserDetails Implementation ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert Set<Role> to Set<SimpleGrantedAuthority>
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
    }

    // `getUsername()` and `getPassword()` are provided by Lombok @Data

    @Override
    public boolean isAccountNonExpired() { return this.accountNonExpired; }

    @Override
    public boolean isAccountNonLocked() { return this.accountNonLocked; }

    @Override
    public boolean isCredentialsNonExpired() { return this.credentialsNonExpired; }

    @Override
    public boolean isEnabled() { return this.enabled; }
}
