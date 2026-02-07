package com.b2b.b2b.modules.crm.pipeline.entity;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.pipelineStage.entity.DealPipelineStage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "deal_pipeline")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DealPipeline extends BasePipeline{

    @OneToMany(mappedBy = "pipeline")
    private List<Deal> deals = new ArrayList<>();

    @OneToMany(mappedBy = "pipeline", cascade = CascadeType.ALL,  orphanRemoval = true)
    @OrderBy("stageOrder ASC")
    private List<DealPipelineStage> pipelineStages = new ArrayList<>();

    public DealPipeline(String pipelineName, boolean isDefault, Organization organization) {
        super(pipelineName, isDefault, organization);
    }
}
