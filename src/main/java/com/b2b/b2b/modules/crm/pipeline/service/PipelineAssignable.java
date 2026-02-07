package com.b2b.b2b.modules.crm.pipeline.service;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.crm.pipeline.entity.BasePipeline;
import com.b2b.b2b.modules.crm.pipelineStage.entity.BasePipelineStage;

public interface PipelineAssignable<P extends BasePipeline, S extends BasePipelineStage> {
    Integer getId();
    Organization getOrganization();
    P getPipeline();
    S getPipelineStage();
    void setPipeline(P pipeline);
    void setPipelineStage(S stage);
}
