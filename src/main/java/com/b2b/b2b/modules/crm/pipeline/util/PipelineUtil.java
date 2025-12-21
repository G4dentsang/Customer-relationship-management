package com.b2b.b2b.modules.crm.pipeline.util;

import com.b2b.b2b.modules.auth.payloads.OrganizationDTO;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineResponseDTO;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class PipelineUtil {

    public PipelineResponseDTO createPipelineResponseDTO(Pipeline pipeline)
    {
        OrganizationDTO org = new  OrganizationDTO(
                pipeline.getOrganization().getOrganizationName(),
                pipeline.getOrganization().getCreatedAt()

        );
        List<PipelineStageResponseDTO> stages = pipeline.getPipelineStages().stream()
                .map(stage -> new PipelineStageResponseDTO(
                        stage.getId(),
                        stage.getStageName(),
                        stage.getStageDescription(),
                        stage.getStageOrder()
                )).toList();

        return  new PipelineResponseDTO(
                pipeline.getId(),
                pipeline.getPipelineName(),
                pipeline.isDefault(),
                pipeline.getCreatedAt(),
                pipeline.getPipelineType(),
                org,
                stages
        );

    }
}
