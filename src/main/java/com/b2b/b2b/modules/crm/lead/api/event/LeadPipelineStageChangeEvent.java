package com.b2b.b2b.modules.crm.lead.api.event;

import com.b2b.b2b.modules.crm.lead.internal.infrastructure.persistence.Lead;
import com.b2b.b2b.modules.crm.pipelineStage.model.LeadPipelineStage;


public record LeadPipelineStageChangeEvent(Lead lead, LeadPipelineStage fromStage, LeadPipelineStage toStage) {
}
