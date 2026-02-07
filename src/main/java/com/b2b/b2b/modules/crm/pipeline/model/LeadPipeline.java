package com.b2b.b2b.modules.crm.pipeline.model;

import com.b2b.b2b.modules.organization.model.Organization;
import com.b2b.b2b.modules.crm.lead.model.Lead;
import com.b2b.b2b.modules.crm.pipelineStage.model.LeadPipelineStage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lead_pipeline")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeadPipeline extends BasePipeline{

    @OneToMany(mappedBy = "pipeline")
    private List<Lead> lead = new ArrayList<>();

    @OneToMany(mappedBy = "pipeline", cascade = CascadeType.ALL,  orphanRemoval = true)
    @OrderBy("stageOrder ASC")
    private List<LeadPipelineStage> pipelineStages = new ArrayList<>();

    public LeadPipeline(String pipelineName, boolean isDefault, Organization organization) {
        super(pipelineName, isDefault, organization);
    }
}
