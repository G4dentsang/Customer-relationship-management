package com.b2b.b2b.modules.crm.pipelineStage.service;

import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;

public interface PipelineStageService {
    PipelineStage findNextPipelineStage(Pipeline  pipeline, Integer currentOrder);
}
