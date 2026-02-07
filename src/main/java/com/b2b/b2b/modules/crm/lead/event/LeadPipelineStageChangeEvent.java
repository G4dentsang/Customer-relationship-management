package com.b2b.b2b.modules.crm.lead.event;

import com.b2b.b2b.modules.crm.lead.model.Lead;
import com.b2b.b2b.modules.crm.pipelineStage.model.LeadPipelineStage;


public record LeadPipelineStageChangeEvent(Lead lead, LeadPipelineStage fromStage, LeadPipelineStage toStage) {
}
