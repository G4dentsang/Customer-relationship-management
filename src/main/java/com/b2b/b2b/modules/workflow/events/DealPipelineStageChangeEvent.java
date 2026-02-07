package com.b2b.b2b.modules.workflow.events;

import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.pipelineStage.entity.DealPipelineStage;


public record DealPipelineStageChangeEvent(Deal deal, DealPipelineStage fromStage, DealPipelineStage toStage) {
}
