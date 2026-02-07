package com.b2b.b2b.modules.crm.pipelineStage.util;

import com.b2b.b2b.modules.crm.pipelineStage.model.BasePipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.model.DealPipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.model.LeadPipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class StageUtils {
    public PipelineStageResponseDTO createPipelineStageResponseDTO(BasePipelineStage stage) {
        String statusValue = getStatusAsString(stage);
        return new PipelineStageResponseDTO(
                stage.getId(),
                stage.getStageName(),
                stage.getStageDescription(),
                stage.getStageOrder(),
                statusValue
        );
    }

    private String getStatusAsString(BasePipelineStage stage) {
        if (stage instanceof LeadPipelineStage leadStage) {
            return leadStage.getMappedStatus().name();
        } else if (stage instanceof DealPipelineStage dealStage) {
            return dealStage.getMappedStatus().name();
        }
        return "UNKNOWN";
    }
}
