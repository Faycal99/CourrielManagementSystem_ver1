package dgb.Mp.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Transient;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
@Setter
public class SecurityUser implements UserDetails {

    private final User user;



    public Long getId() {
        return user.getId();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // Return the user's authorities (roles and privileges)
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return user.getRole().getPrivileges().stream()
//                .map(privilege -> (new SimpleGrantedAuthority(privilege.getName().name())))
//                .collect(Collectors.toSet());// Correct placement of parentheses
//
//    }


//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        Set<GrantedAuthority> authorities = new HashSet<>();
//        // Add role-based authority
//        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getId()));
//        // Add privilege-based authorities
//        authorities.addAll(user.getRole().getPrivileges().stream()
//                .map(privilege -> new SimpleGrantedAuthority(privilege.getName().name()))
//                .collect(Collectors.toSet()));
//        return authorities;
//    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Map privileges to SimpleGrantedAuthority
        return user.getRole().getPrivileges().stream()
                .map(priv -> new SimpleGrantedAuthority(priv.getName().toString()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    public User getUser() {
        return user;
    }
}
