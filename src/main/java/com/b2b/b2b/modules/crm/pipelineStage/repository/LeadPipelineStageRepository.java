package com.b2b.b2b.modules.crm.pipelineStage.repository;

import com.b2b.b2b.modules.crm.pipeline.entity.LeadPipeline;
import com.b2b.b2b.modules.crm.pipelineStage.entity.LeadPipelineStage;

import java.util.List;

public interface LeadPipelineStageRepository extends BasePipelineStageRepository<LeadPipelineStage, LeadPipeline>
{
    List<LeadPipelineStage> findAllByPipelineOrderByStageOrderAsc(LeadPipeline pipeline);
}
