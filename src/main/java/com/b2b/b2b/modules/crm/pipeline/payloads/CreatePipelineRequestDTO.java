package com.b2b.b2b.modules.crm.pipeline.payloads;

import com.b2b.b2b.modules.crm.pipeline.entity.PipelineType;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageRequestDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class CreatePipelineRequestDTO {

    @NotBlank(message = "Pipeline name is required")
    @Size(max = 100)
    private String pipelineName;

    private boolean isDefault;
    @NotBlank(message = "pipeline type is required for the pipeline")
    private PipelineType pipelineType;

    private List<PipelineStageRequestDTO> stages = new ArrayList<>();


}
