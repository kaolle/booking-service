package pb.se.bookingservice.port.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private static final long serialVersionUID = 1L;
    private String username;
    private String memberId;

    private String email;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;


    public CustomUserDetails(String username, String memberId, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.memberId = memberId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getMemberId() {
        return memberId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // TODO implement logic for account expiration here
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // TODO implement logic for account locking here
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // TODO implement logic for credentials expiration here
    }

    @Override
    public boolean isEnabled() {
        return true; // TODO implement logic for user enablement here
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CustomUserDetails that = (CustomUserDetails) o;

        return new EqualsBuilder().append(memberId, that.memberId).append(username, that.username).append(email, that.email).append(password, that.password).append(authorities, that.authorities).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(memberId).append(username).append(email).append(password).append(authorities).toHashCode();
    }
}
