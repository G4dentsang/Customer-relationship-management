package com.b2b.b2b.modules.auth.entity;

import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipelines;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
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
    @Column(name="org_id")
    private Integer organizationId;
    private String organizationName;
    private LocalDate createdAt;
    @OneToMany(mappedBy = "organization")
    private List<Lead> leads = new ArrayList<>();
    @OneToMany(mappedBy = "organization")
    private List<Company> companies = new ArrayList<>();
    @OneToMany(mappedBy = "organization")
    private List<Pipelines> pipelines = new ArrayList<>();
    @OneToMany(mappedBy = "organization")
    private List<UserOrganization> userOrganizations = new ArrayList<>();
    @OneToMany(mappedBy = "organization")
    private List<WorkflowRule> workflowRule = new ArrayList<>();
    public Organization(String organizationName, LocalDate createdAt) {
        this.organizationName = organizationName;
        this.createdAt = createdAt;
    }
}
