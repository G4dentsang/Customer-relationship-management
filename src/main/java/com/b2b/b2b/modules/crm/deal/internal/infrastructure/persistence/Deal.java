package com.b2b.b2b.modules.crm.deal.internal.infrastructure.persistence;

import com.b2b.b2b.modules.user.model.User;
import com.b2b.b2b.modules.crm.company.internal.entity.Company;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.persistence.Lead;
import com.b2b.b2b.modules.crm.pipeline.model.DealPipeline;
import com.b2b.b2b.modules.crm.pipeline.service.PipelineAssignable;
import com.b2b.b2b.modules.crm.pipelineStage.model.DealPipelineStage;
import com.b2b.b2b.modules.workflow.defination.service.WorkflowTarget;
import com.b2b.b2b.shared.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "deal")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Deal extends BaseEntity implements PipelineAssignable<DealPipeline, DealPipelineStage>, WorkflowTarget {

    @NotBlank(message = "Deal name is required")
    @Size(max = 100)
    private String dealName;

    @Column(name = "deal_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal dealAmount = BigDecimal.ZERO;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "deal_status", nullable = false, length = 50)
    private DealStatus dealStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
    @NotNull(message = "Lead is required")
    private Lead lead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deal_pipeline_id")
    private DealPipeline pipeline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deal_stage_id")
    private DealPipelineStage pipelineStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;

}
