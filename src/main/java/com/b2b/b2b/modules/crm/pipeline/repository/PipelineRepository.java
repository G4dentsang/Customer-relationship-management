package com.b2b.b2b.modules.crm.pipeline.repository;

import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipeline.entity.PipelineType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PipelineRepository extends JpaRepository<Pipeline, Integer>, JpaSpecificationExecutor<Pipeline>
{
    Optional<Pipeline> findByPipelineTypeAndIsDefaultTrue(PipelineType type);
}
