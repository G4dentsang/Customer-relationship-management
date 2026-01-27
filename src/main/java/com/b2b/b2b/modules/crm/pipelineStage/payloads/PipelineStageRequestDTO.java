package com.b2b.b2b.modules.crm.pipelineStage.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PipelineStageRequestDTO {
    @NotBlank(message = "Pipeline stage name is required")
    @Size(max = 100)
    private String stageName;
    @Size(max = 255)
    private String stageDescription;
    @NotBlank(message = "Pipeline stage order is required")
    private Integer stageOrder;
}
