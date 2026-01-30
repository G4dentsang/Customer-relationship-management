package com.b2b.b2b.modules.crm.pipelineStage.entity;

import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.shared.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PipelineStage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pipline_stage_id",  nullable = false)
    private Integer id;

    @NotBlank(message = "Pipeline stage name is required")
    @Size(max = 100)
    @Column(name = "stage_name", nullable = false, length = 100)
    private String stageName;

    @Size(max = 255)
    @Column(name = "stage_desc",  length = 255)
    private String stageDescription;

    @NotNull(message = "Pipeline stage order is required")
    @Column(name = "stage_order", nullable = false)
    private Integer stageOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_id", nullable = false)
    private Pipeline pipeline;

    @OneToMany(mappedBy = "pipelineStage")
    private List<Lead> leads;
    @OneToMany(mappedBy = "pipelineStage")
    private List<Deal> deals;

    @PrePersist
    public void prePersist()
    {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public PipelineStage(String stageName, String stageDescription, Integer stageOrder, LocalDateTime createdAt, Pipeline pipeline) {
        this.stageName = stageName;
        this.stageDescription = stageDescription;
        this.stageOrder = stageOrder;
        this.createdAt = createdAt;
        this.pipeline = pipeline;
    }
}
