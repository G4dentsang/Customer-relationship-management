package com.b2b.b2b.modules.crm.pipeline.repository;

import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PipelineRepository extends JpaRepository<Pipeline, Integer>
{
    @Query("Select p FROM Pipeline p WHERE p.organization.organizationId = :orgId")
    Pipeline findDefaultPipelineByOrganizationId(@Param("orgId") Integer organizationId);
}
