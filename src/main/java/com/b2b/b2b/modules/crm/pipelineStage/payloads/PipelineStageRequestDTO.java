package com.b2b.b2b.modules.crm.pipelineStage.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PipelineStageRequestDTO {

    @NotBlank(message = "Pipeline stage name is required")
    @Size(max = 100)
    private String stageName;

    @Size(max = 255)
    private String stageDescription;

    @NotBlank(message = "Pipeline stage order is required")
    private Integer stageOrder;

    @NotBlank(message = "Mapped status is required (e.g., 'NEW', 'OPEN', 'WON')")
    private String mappedStatus;
}
