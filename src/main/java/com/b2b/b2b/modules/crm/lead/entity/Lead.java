package com.b2b.b2b.modules.crm.lead.entity;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipeline.service.PipelineAssignable;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import com.b2b.b2b.modules.workflow.service.WorkflowTarget;
import com.b2b.b2b.shared.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
public class Lead extends BaseEntity implements PipelineAssignable, WorkflowTarget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lead_id", nullable = false)
    private Integer id;

    @NotBlank(message = "Lead name is required")
    @Size(max = 100)
    @Column(name = "lead_name", nullable = false, length = 100)
    private String leadName;

    @Email(message = "Please provide a valid email address")
    @Size(max = 255)
    @NotBlank(message = "Email is required")
    @Column(name = "lead_email",  nullable = false, length = 255)
    private String leadEmail;

    @Pattern(regexp = "^\\+?[0-9.]{7,15}$", message = "Invalid phone number format")
    @Column(name = "lead_phone", length = 20)
    private String leadPhone;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "lead_status", nullable = false, length = 50)
    private LeadStatus leadStatus = LeadStatus.NEW;

    @Column(name = "created_at", nullable = false, updatable = false, length = 100)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "converted_at")
    private LocalDateTime convertedAt;

    @Column(nullable = false)
    private boolean readyForConversion = false;

    @Column(nullable = false)
    private boolean isConverted = false;

    /*******************GDPR Fields*********************/
    @Column(name = "gdpr_consent_given", nullable = false)
    private boolean gdprConsentGiven = false; // automated email

    private LocalDateTime gdprDataProcessingConsent; // time terms agreement
    private LocalDateTime gdprErasedAt; // time deletion

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    @NotNull(message = "Organization is required")
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_id")
    private Pipeline pipeline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_stage_id")
    private PipelineStage pipelineStage;

    @OneToMany(mappedBy = "lead")
    private List<Deal> deals = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;

    public Lead(String leadEmail, String leadName, String leadPhone) {
        this.leadEmail = leadEmail;
        this.leadName = leadName;
        this.leadPhone = leadPhone;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void markAsConverted(){
        if(this.isConverted) return; //Idempotency check
        this.leadStatus = LeadStatus.CONVERTED;
        this.isConverted = true;
        this.convertedAt = LocalDateTime.now();
    }


}
