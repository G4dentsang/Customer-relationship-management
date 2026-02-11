package com.b2b.b2b.modules.crm.company.internal.entity;

import com.b2b.b2b.modules.organization.model.Organization;
import com.b2b.b2b.modules.crm.contact.internal.entity.Contact;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.persistence.Deal;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.persistence.Lead;
import com.b2b.b2b.shared.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;


import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Company extends BaseEntity {
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
        this.setOrganization(organization);
    }

}
