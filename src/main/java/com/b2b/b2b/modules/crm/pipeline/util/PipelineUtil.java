package com.b2b.b2b.modules.crm.pipeline.util;

import com.b2b.b2b.modules.auth.payloads.OrganizationDTO;
import com.b2b.b2b.modules.crm.pipeline.entity.BasePipeline;
import com.b2b.b2b.modules.crm.pipeline.entity.DealPipeline;
import com.b2b.b2b.modules.crm.pipeline.entity.LeadPipeline;
import com.b2b.b2b.modules.crm.pipeline.entity.PipelineType;
import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineResponseDTO;
import com.b2b.b2b.modules.crm.pipelineStage.entity.BasePipelineStage;
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
