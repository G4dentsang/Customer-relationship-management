package com.b2b.b2b.modules.crm.pipeline.service;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;

public interface PipelineAssignable {
    Integer getId();
    Organization getOrganization();
    Pipeline getPipeline();
    PipelineStage getPipelineStage();
    void setPipeline(Pipeline pipeline);
    void setPipelineStage(PipelineStage stage);
}
