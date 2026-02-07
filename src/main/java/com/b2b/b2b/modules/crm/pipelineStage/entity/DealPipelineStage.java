package com.b2b.b2b.modules.crm.pipelineStage.entity;

import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.deal.entity.DealStatus;
import com.b2b.b2b.modules.crm.pipeline.entity.DealPipeline;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "deal_stage")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DealPipelineStage extends BasePipelineStage {
    @Enumerated(EnumType.STRING)
    @Column(name = "mapped_status", nullable = false, length = 50)
    private DealStatus mappedStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deal_pipeline_id", nullable = false)
    private DealPipeline pipeline;

    @OneToMany(mappedBy = "pipelineStage")
    private List<Deal> deals = new ArrayList<>();

    public DealPipelineStage(String stageName, Integer stageOrder, DealPipeline pipeline, DealStatus mappedStatus ) {
        super(stageName, "Stage for " + stageName, stageOrder);
        this.mappedStatus = mappedStatus;
        this.pipeline = pipeline;
    }
}
