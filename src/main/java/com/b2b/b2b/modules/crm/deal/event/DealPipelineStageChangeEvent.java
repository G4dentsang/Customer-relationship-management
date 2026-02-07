package com.b2b.b2b.modules.crm.deal.event;

import com.b2b.b2b.modules.crm.deal.model.Deal;
import com.b2b.b2b.modules.crm.pipelineStage.model.DealPipelineStage;


public record DealPipelineStageChangeEvent(Deal deal, DealPipelineStage fromStage, DealPipelineStage toStage) {
}
