package com.b2b.b2b.modules.crm.pipelineStage.service.impl;

import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.repository.PipelineStageRepository;
import com.b2b.b2b.modules.crm.pipelineStage.service.PipelineStageService;
import org.springframework.stereotype.Service;

@Service
public class PipelineStageServiceImpl implements PipelineStageService
{
    private final PipelineStageRepository pipelineStageRepository;

    public PipelineStageServiceImpl(PipelineStageRepository pipelineStageRepository) {
        this.pipelineStageRepository = pipelineStageRepository;
    }

    @Override
    public PipelineStage findNextPipelineStage(Pipeline pipeline, Integer currentOrder) {
    return pipelineStageRepository.findNextStages(pipeline,currentOrder)
            .stream()
            .findFirst()
            .orElse(null);
    }
}
