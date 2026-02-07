package com.b2b.b2b.modules.organization.model;

import com.b2b.b2b.modules.crm.company.model.Company;
import com.b2b.b2b.modules.crm.lead.model.Lead;
import com.b2b.b2b.modules.crm.pipeline.model.DealPipeline;
import com.b2b.b2b.modules.crm.pipeline.model.LeadPipeline;
import com.b2b.b2b.modules.workflow.defination.model.WorkflowRule;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="organizations")
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer organizationId;

    private String organizationName;

    private LocalDate createdAt;

    @OneToMany(mappedBy = "organization")
    private List<Lead> leads = new ArrayList<>();

    @OneToMany(mappedBy = "organization")
    private List<Company> companies = new ArrayList<>();

    @OneToMany(mappedBy = "organization")
    private List<LeadPipeline> leadPipeline = new ArrayList<>();

    @OneToMany(mappedBy = "organization")
    private List<DealPipeline> dealPipeline = new ArrayList<>();

    @OneToMany(mappedBy = "organization")
    private List<UserOrganization> userOrganizations = new ArrayList<>();

    @OneToMany(mappedBy = "organization")
    private List<WorkflowRule> workflowRule = new ArrayList<>();

    public Organization(String organizationName) {
        this.organizationName = organizationName;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDate.now();
    }
}
