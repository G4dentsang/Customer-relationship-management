package com.b2b.b2b.modules.crm.pipelineStage.service;

import com.b2b.b2b.modules.crm.deal.model.Deal;
import com.b2b.b2b.modules.crm.pipeline.model.DealPipeline;
import com.b2b.b2b.modules.crm.pipelineStage.model.DealPipelineStage;

public interface DealPipelineStageService extends PipelineStageOperations<DealPipeline, DealPipelineStage> {
    void assignDefaultStage(DealPipeline pipeline, Deal deal);
}
