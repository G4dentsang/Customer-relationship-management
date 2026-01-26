package com.b2b.b2b.modules.crm.pipelineStage.payloads;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PipelineStageFilterDTO {
    private Integer pipelineId;
    private String searchText;
}
