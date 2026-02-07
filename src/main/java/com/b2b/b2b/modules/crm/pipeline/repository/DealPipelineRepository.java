package com.b2b.b2b.modules.crm.pipeline.repository;

import com.b2b.b2b.modules.crm.pipeline.entity.DealPipeline;

import java.util.Optional;


public interface DealPipelineRepository extends BasePipelineRepository<DealPipeline>
{
    Optional<DealPipeline> findByIsDefaultTrue();
}
