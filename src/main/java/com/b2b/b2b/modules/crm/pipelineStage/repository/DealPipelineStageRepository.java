package com.b2b.b2b.modules.crm.pipelineStage.repository;

import com.b2b.b2b.modules.crm.pipeline.entity.DealPipeline;
import com.b2b.b2b.modules.crm.pipelineStage.entity.DealPipelineStage;

import java.util.List;


public interface DealPipelineStageRepository extends BasePipelineStageRepository<DealPipelineStage , DealPipeline>
{
    List<DealPipelineStage> findAllByPipelineOrderByStageOrderAsc(DealPipeline pipeline);

}
