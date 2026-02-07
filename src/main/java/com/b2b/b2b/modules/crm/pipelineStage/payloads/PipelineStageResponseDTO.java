package com.b2b.b2b.modules.crm.pipelineStage.payloads;

public record PipelineStageResponseDTO(
        Integer id,
        String stageName,
        String stageDescription,
        Integer stageOrder,
        String status
) {
}
