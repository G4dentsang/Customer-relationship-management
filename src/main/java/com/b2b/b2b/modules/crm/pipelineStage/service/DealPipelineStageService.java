package com.b2b.b2b.modules.crm.pipelineStage.service;

import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.pipeline.entity.DealPipeline;
import com.b2b.b2b.modules.crm.pipelineStage.entity.DealPipelineStage;

public interface DealPipelineStageService extends PipelineStageOperations<DealPipeline, DealPipelineStage> {
    void assignDefaultStage(DealPipeline pipeline, Deal deal);
}
