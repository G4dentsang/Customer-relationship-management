package com.b2b.b2b.modules.crm.lead.entity;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.pipeline.entity.LeadPipeline;
import com.b2b.b2b.modules.crm.pipeline.service.PipelineAssignable;
import com.b2b.b2b.modules.crm.pipelineStage.entity.LeadPipelineStage;
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
@Table(name = "lead")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Lead extends BaseEntity implements PipelineAssignable<LeadPipeline, LeadPipelineStage>, WorkflowTarget {

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

    private String lossReason;

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
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_pipeline_id")
    private LeadPipeline pipeline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_stage_id")
    private LeadPipelineStage pipelineStage;

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

    public void markAsConverted(){
        if(this.isConverted) return; //Idempotency check
        this.leadStatus = LeadStatus.CONVERTED;
        this.isConverted = true;
        this.convertedAt = LocalDateTime.now();
    }
}
