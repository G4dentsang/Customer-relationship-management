package com.b2b.b2b.modules.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="organizations")
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="org_id")
    private Integer organizationId;
    private String organizationName;
    private LocalDate createdAt;
    @OneToMany(mappedBy = "organization")
    private List<UserOrganization> userOrganizations = new ArrayList<>();
}
