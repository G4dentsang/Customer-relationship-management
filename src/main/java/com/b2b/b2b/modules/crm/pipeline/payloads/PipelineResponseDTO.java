package com.b2b.b2b.modules.crm.pipeline.payloads;

import com.b2b.b2b.modules.organization.payload.OrganizationDTO;
import com.b2b.b2b.modules.crm.pipeline.model.PipelineType;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public record PipelineResponseDTO(
        Integer id,
        String pipelineName,
        boolean isDefault,
        LocalDateTime createdAt,
        PipelineType pipelineType,
        OrganizationDTO organization,
        List<PipelineStageResponseDTO> stages
) {

}
