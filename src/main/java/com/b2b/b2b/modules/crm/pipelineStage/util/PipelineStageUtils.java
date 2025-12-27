package com.b2b.b2b.modules.crm.pipelineStage.util;

import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class PipelineStageUtils {
    public PipelineStageResponseDTO createPipelineStageResponseDTO(PipelineStage stage)
    {
        return new PipelineStageResponseDTO(
                stage.getId(),
                stage.getStageName(),
                stage.getStageDescription(),
                stage.getStageOrder()
        );
    }
}
