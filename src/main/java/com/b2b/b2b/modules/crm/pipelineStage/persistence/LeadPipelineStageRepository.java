package com.b2b.b2b.modules.crm.pipelineStage.persistence;

import com.b2b.b2b.modules.crm.pipeline.model.LeadPipeline;
import com.b2b.b2b.modules.crm.pipelineStage.model.LeadPipelineStage;

import java.util.List;

public interface LeadPipelineStageRepository extends BasePipelineStageRepository<LeadPipelineStage, LeadPipeline>
{
    List<LeadPipelineStage> findAllByPipelineOrderByStageOrderAsc(LeadPipeline pipeline);
}
