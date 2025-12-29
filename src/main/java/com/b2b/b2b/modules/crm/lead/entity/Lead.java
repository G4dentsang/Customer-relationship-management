package com.b2b.b2b.modules.crm.lead.entity;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipeline.service.PipelineAssignable;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import com.b2b.b2b.modules.workflow.service.WorkflowTarget;
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
public class Lead implements PipelineAssignable, WorkflowTarget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String leadName;
    private String leadEmail;
    private String leadPhone;
    @Enumerated(EnumType.STRING)
    private LeadStatus leadStatus = LeadStatus.NEW;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;//lead
    private LocalDateTime convertedAt;//to deal
    private boolean readyForConversion = false;
    private boolean isConverted = false;
    //GDPR....
    private boolean gdpr_is_consent_given = false; // automated email
    private LocalDateTime gdpr_data_processing_consent; // time terms agreement
    private LocalDateTime gdpr_erased_at; // time deletion

    @ManyToOne
    private Organization organization;

    @ManyToOne
    private Company company;

    @ManyToOne
    private Pipeline pipeline;

    @ManyToOne
    private PipelineStage pipelineStage;

    @OneToMany(mappedBy = "lead")
    private List<Deal> deals = new ArrayList<>();

    @ManyToOne
    private User assignedUser;

    public Lead(String leadEmail, String leadName, String leadPhone) {
        this.leadEmail = leadEmail;
        this.leadName = leadName;
        this.leadPhone = leadPhone;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void markAsConverted(){
        this.leadStatus = LeadStatus.CONVERTED;
        this.isConverted = true;
        this.convertedAt = LocalDateTime.now();
    }


}
