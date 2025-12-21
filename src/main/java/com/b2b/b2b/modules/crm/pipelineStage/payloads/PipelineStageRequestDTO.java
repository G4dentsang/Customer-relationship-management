package com.b2b.b2b.modules.crm.pipelineStage.payloads;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PipelineStageRequestDTO {
    private String stageName;
    private String stageDescription;
    private Integer stageOrder;
}
