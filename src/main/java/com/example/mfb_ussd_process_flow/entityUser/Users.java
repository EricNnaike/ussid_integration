package com.example.mfb_ussd_process_flow.entityUser;

import com.example.mfb_ussd_process_flow.enums.RoleType;
import com.example.mfb_ussd_process_flow.enums.StatusConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "ussid_users")
public class Users implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$",message = "Invalid Email Address")
    @Column(unique = true, name = "username")
    private String username;

    @Column(name = "secretKey")
    private String secretKey;

    @Column(name = "password")
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusConstant status;

    private boolean enabled = false;

    @JsonManagedReference
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Token> token = new ArrayList<>();

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Basic
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    public Users(String username, String password) {
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
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


}
