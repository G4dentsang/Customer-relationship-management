package com.b2b.b2b.modules.crm.company.entity;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.crm.contact.entity.Contact;
import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;


import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FilterDef(
        name = "organizationFilter",
        parameters = @ParamDef(name = "orgId", type = Integer.class)
)
@Filter(
        name = "organizationFilter",
        condition = "organization_id = :orgId"
)
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String companyName;
    private String website;
    private String industry;
    private String city;
    private String state;
    private String country;
    private Boolean isDeleted;
    @ManyToOne
    private Organization organization;
    @OneToMany(mappedBy = "company")
    private List<Deal> deals = new ArrayList<>();
    @OneToMany(mappedBy = "company")
    private List<Contact> contacts = new ArrayList<>();
    @OneToMany(mappedBy = "company")
    private List<Lead> leads = new ArrayList<>();

    public Company(String companyName, String website, String industry, Organization organization) {
        this.companyName = companyName;
        this.website = website;
        this.industry = industry;
        this.organization = organization;
    }

}
