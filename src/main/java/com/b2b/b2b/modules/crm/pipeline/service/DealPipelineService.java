package com.b2b.b2b.modules.crm.pipeline.service;

import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.pipeline.entity.DealPipeline;

public interface DealPipelineService extends PipelineOperations<DealPipeline> {
    DealPipeline assignDefaultPipeline(Deal deal);
}
