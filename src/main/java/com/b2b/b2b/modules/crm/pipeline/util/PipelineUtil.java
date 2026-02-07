package com.b2b.b2b.modules.crm.pipeline.util;

import com.b2b.b2b.modules.organization.payload.OrganizationDTO;
import com.b2b.b2b.modules.crm.pipeline.model.BasePipeline;
import com.b2b.b2b.modules.crm.pipeline.model.DealPipeline;
import com.b2b.b2b.modules.crm.pipeline.model.LeadPipeline;
import com.b2b.b2b.modules.crm.pipeline.model.PipelineType;
import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineResponseDTO;
import com.b2b.b2b.modules.crm.pipelineStage.model.BasePipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageResponseDTO;
import com.b2b.b2b.modules.crm.pipelineStage.util.StageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@RequiredArgsConstructor
public class PipelineUtil {

    private final StageUtils stageUtils;

    public PipelineResponseDTO createPipelineResponseDTO(BasePipeline pipeline)
    {
        List<? extends BasePipelineStage> stages;
        PipelineType type;

        if(pipeline instanceof LeadPipeline leadPipeline){
            stages = leadPipeline.getPipelineStages();
            type = PipelineType.LEAD;

        }
        else if(pipeline instanceof DealPipeline  dealPipeline){
            stages = dealPipeline.getPipelineStages();
            type = PipelineType.DEAL;
        }
        else{
            throw new IllegalArgumentException("Invalid pipeline type");
        }

        OrganizationDTO org = new  OrganizationDTO(
                pipeline.getOrganization().getOrganizationName(),
                pipeline.getOrganization().getCreatedAt()

        );
        List<PipelineStageResponseDTO> stageDTOs = stages.stream()
                .map(stageUtils::createPipelineStageResponseDTO)
                .toList();

        return  new PipelineResponseDTO(
                pipeline.getId(),
                pipeline.getPipelineName(),
                pipeline.isDefault(),
                pipeline.getCreatedAt(),
                type,
                org,
                stageDTOs
        );

    }
}
