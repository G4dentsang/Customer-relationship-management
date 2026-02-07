package com.b2b.b2b.modules.crm.pipelineStage.model;

import com.b2b.b2b.modules.crm.lead.model.Lead;
import com.b2b.b2b.modules.crm.lead.model.LeadStatus;
import com.b2b.b2b.modules.crm.pipeline.model.LeadPipeline;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lead_stage")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeadPipelineStage extends BasePipelineStage {

    @Enumerated(EnumType.STRING)
    @Column(name = "mapped_status", nullable = false, length = 50)
    private LeadStatus mappedStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_pipeline_id", nullable = false)
    private LeadPipeline pipeline;

    @OneToMany(mappedBy = "pipelineStage")
    private List<Lead> leads = new ArrayList<>();

    public LeadPipelineStage(String stageName, Integer stageOrder, LeadPipeline pipeline, LeadStatus mappedStatus) {
        super(stageName, "Stage for " + stageName, stageOrder);
        this.pipeline = pipeline;
        this.mappedStatus = mappedStatus;
    }

    private void syncOrganization() {
        if (this.pipeline != null) {
            this.setOrganization(this.pipeline.getOrganization());
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        syncOrganization();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        syncOrganization();
    }

}
