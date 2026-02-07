package com.b2b.b2b.modules.crm.pipelineStage.service;

import com.b2b.b2b.modules.crm.lead.model.Lead;
import com.b2b.b2b.modules.crm.pipeline.model.LeadPipeline;
import com.b2b.b2b.modules.crm.pipelineStage.model.LeadPipelineStage;

public interface LeadPipelineStageService extends PipelineStageOperations<LeadPipeline, LeadPipelineStage> {
    void assignDefaultStage(LeadPipeline pipeline, Lead lead);
}
