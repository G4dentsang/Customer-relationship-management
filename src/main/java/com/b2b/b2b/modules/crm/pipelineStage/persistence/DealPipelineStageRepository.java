package com.b2b.b2b.modules.crm.pipelineStage.persistence;

import com.b2b.b2b.modules.crm.pipeline.model.DealPipeline;
import com.b2b.b2b.modules.crm.pipelineStage.model.DealPipelineStage;

import java.util.List;


public interface DealPipelineStageRepository extends BasePipelineStageRepository<DealPipelineStage , DealPipeline>
{
    List<DealPipelineStage> findAllByPipelineOrderByStageOrderAsc(DealPipeline pipeline);

}
