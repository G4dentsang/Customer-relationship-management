package com.b2b.b2b.modules.crm.company.entity;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.crm.contact.entity.Contacts;
import com.b2b.b2b.modules.crm.deal.entity.Deals;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
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
    private String website;
    private String industry;
    @ManyToOne
    private Organization organization;
    @OneToMany(mappedBy = "company")
    private List<Deals> deals = new ArrayList<>();
    @OneToMany(mappedBy = "company")
    private List<Contacts> contacts = new ArrayList<>();
    @OneToMany(mappedBy = "company")
    private List<Lead> leads = new ArrayList<>();

    public Company(String companyName, String website, String industry, Organization organization) {
        this.companyName = companyName;
        this.website = website;
        this.industry = industry;
        this.organization = organization;
    }

}
