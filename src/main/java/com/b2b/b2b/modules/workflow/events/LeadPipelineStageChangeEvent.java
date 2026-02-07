package com.b2b.b2b.modules.workflow.events;

import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.pipelineStage.entity.LeadPipelineStage;


public record LeadPipelineStageChangeEvent(Lead lead, LeadPipelineStage fromStage, LeadPipelineStage toStage) {
}
