package com.b2b.b2b.modules.crm.company.entity;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.crm.contact.entity.Contacts;
import com.b2b.b2b.modules.crm.deal.entity.Deals;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String companyName;
    private LocalDateTime createdAt;
    private String website;
    private String industry;
    @ManyToOne
    private Organization organization;
    @OneToMany(mappedBy = "company")
    private List<Deals> deals = new ArrayList<>();
    @OneToMany(mappedBy = "company")
    private List<Contacts> contacts = new ArrayList<>();
    @PrePersist
    protected void onCreate() { //JPA calls this method automatically before saving
        this.createdAt = LocalDateTime.now();
    }

}
