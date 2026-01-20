package com.b2b.b2b.modules.crm.pipelineStage.entity;

import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.time.LocalDateTime;
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
public class PipelineStage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String stageName;
    private String stageDescription;
    private Integer stageOrder;
    private LocalDateTime createdAt;
    @ManyToOne
    private Pipeline pipeline;
    @OneToMany(mappedBy = "pipelineStage")
    private List<Lead> leads;
    @OneToMany(mappedBy = "pipelineStage")
    private List<Deal> deals;

    @PrePersist
    public void prePersist()
    {
        this.createdAt = LocalDateTime.now();
    }

    public PipelineStage(String stageName, String stageDescription, Integer stageOrder, LocalDateTime createdAt, Pipeline pipeline) {
        this.stageName = stageName;
        this.stageDescription = stageDescription;
        this.stageOrder = stageOrder;
        this.createdAt = createdAt;
        this.pipeline = pipeline;
    }
}
