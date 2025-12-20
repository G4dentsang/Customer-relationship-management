package com.b2b.b2b.modules.crm.deal.entity;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
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

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Deals implements PipelineAssignable, WorkflowTarget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String dealName;
    private Double dealAmount = 0.00;
    @Enumerated(EnumType.STRING)
    private DealStatus dealStatus;
    private LocalDateTime closedAt;
    private LocalDateTime createdAt;
    @ManyToOne
    private Lead lead;
    @ManyToOne
    private Company company;
    @ManyToOne
    private Organization organization;
    @ManyToOne
    private Pipeline pipeline;
    @ManyToOne
    private PipelineStage pipelineStage;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
