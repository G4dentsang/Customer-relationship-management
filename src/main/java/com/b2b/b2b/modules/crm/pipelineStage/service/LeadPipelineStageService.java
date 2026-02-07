package com.b2b.b2b.modules.crm.pipelineStage.service;

import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.pipeline.entity.LeadPipeline;
import com.b2b.b2b.modules.crm.pipelineStage.entity.LeadPipelineStage;

public interface LeadPipelineStageService extends PipelineStageOperations<LeadPipeline, LeadPipelineStage> {
    void assignDefaultStage(LeadPipeline pipeline, Lead lead);
}
