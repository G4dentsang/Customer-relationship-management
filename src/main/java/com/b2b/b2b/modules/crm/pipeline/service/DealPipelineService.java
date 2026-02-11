package com.b2b.b2b.modules.crm.pipeline.service;

import com.b2b.b2b.modules.crm.deal.internal.infrastructure.persistence.Deal;
import com.b2b.b2b.modules.crm.pipeline.model.DealPipeline;

public interface DealPipelineService extends PipelineOperations<DealPipeline> {
    DealPipeline assignDefaultPipeline(Deal deal);
}
