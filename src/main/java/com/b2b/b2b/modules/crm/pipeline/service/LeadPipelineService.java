package com.b2b.b2b.modules.crm.pipeline.service;

import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.pipeline.entity.LeadPipeline;

public interface LeadPipelineService extends PipelineOperations<LeadPipeline> {
    LeadPipeline assignDefaultPipeline(Lead lead);
}
