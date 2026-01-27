package com.b2b.b2b.modules.crm.company.entity;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.crm.contact.entity.Contact;
import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.validator.constraints.URL;


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
    @Column(name = "company_id", nullable = false)
    private Integer id;

    @NotBlank(message = "Company name is required")
    @Size(max = 100)
    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @URL(message = "Invalid website URL")
    @Column(name = "company_website", length = 255)
    private String website;

    @NotBlank(message = "Company's industry is required")
    @Column(name = "company_industry", nullable = false, length = 255)
    private String industry;

    @Column(name = "company_city", length = 100)
    private String city;

    @Column(name = "company_state", length = 100)
    private String state;

    @Column(name = "company_country", length = 100)
    private String country;

    @Column(name = "is_Deleted")
    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
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
