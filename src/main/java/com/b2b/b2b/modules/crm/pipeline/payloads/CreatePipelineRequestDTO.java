package com.b2b.b2b.modules.crm.pipeline.payloads;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.crm.pipeline.entity.PipelineType;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageRequestDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Data
@Getter
@Setter
public class CreatePipelineRequestDTO {
    private String pipelineName;
    private boolean isDefault;
    private PipelineType pipelineType;
    private Organization organization;
    private List<PipelineStageRequestDTO> stages = new ArrayList<>();


}
