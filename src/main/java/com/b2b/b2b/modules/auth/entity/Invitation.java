package com.b2b.b2b.modules.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String token;
    private LocalDateTime expiryDate;
    private boolean accepted = false;

    @ManyToOne()
    private Organization organization;
    @ManyToOne()
    private Role role;

    public Invitation(Role role, Organization organization, String token, String email) {
        this.role = role;
        this.organization = organization;
        this.expiryDate = LocalDateTime.now().plusDays(7);
        this.token = token;
        this.email = email;
    }
}
