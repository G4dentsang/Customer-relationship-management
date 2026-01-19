package com.b2b.b2b.modules.auth.entity;

import com.b2b.b2b.modules.crm.lead.entity.Lead;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @NotBlank
    @Size(max = 20)
    @Column(name = "username")
    private String userName;

    @NotBlank
    @Size(max = 120)
    @Column(name = "password")
    private String password;

    @NotBlank
    @Size(max = 50)
    @Email
    @Column(name = "email")
    private String email;

    private boolean userActive;
    private boolean emailVerified;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user")
    private List<UserOrganization> userOrganizations = new ArrayList<>();
    @OneToMany(mappedBy = "assignedUser")
    private  List<Lead> leads = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public User(String userName, String email, String password ) {
        this.email = email;
        this.password = password;
        this.userName = userName;
    }


//    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
//    private List<Address> addresses = new ArrayList<>();

}
