package com.b2b.b2b.modules.crm.pipeline.service;

import com.b2b.b2b.modules.crm.lead.model.Lead;
import com.b2b.b2b.modules.crm.pipeline.model.LeadPipeline;

public interface LeadPipelineService extends PipelineOperations<LeadPipeline> {
    LeadPipeline assignDefaultPipeline(Lead lead);
}
