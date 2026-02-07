package com.b2b.b2b.modules.crm.pipeline.persistence;

import com.b2b.b2b.modules.crm.pipeline.model.DealPipeline;

import java.util.Optional;


public interface DealPipelineRepository extends BasePipelineRepository<DealPipeline>
{
    Optional<DealPipeline> findByIsDefaultTrue();
}
