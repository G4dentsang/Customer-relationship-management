package com.b2b.b2b.modules.workflow.events;

import com.b2b.b2b.modules.crm.pipeline.service.PipelineAssignable;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PipelineStageChangeEvent {
    private final PipelineAssignable entity;
    private final PipelineStage fromStage;
    private final PipelineStage toStage;
    public PipelineStageChangeEvent(PipelineAssignable entity, PipelineStage fromStage, PipelineStage toStage) {
        this.entity = entity;
        this.fromStage = fromStage;
        this.toStage = toStage;
    }
}
