package com.b2b.b2b.modules.crm.pipeline.payloads;

import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageRequestDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class CreatePipelineRequestDTO2 {

    @NotBlank(message = "Pipeline name is required")
    @Size(max = 100)
    private String name;

    private boolean isDefault;

    private List<PipelineStageRequestDTO> stages = new ArrayList<>();


}
