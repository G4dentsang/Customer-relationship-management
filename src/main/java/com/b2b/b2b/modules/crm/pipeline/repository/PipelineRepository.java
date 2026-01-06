package com.b2b.b2b.modules.crm.pipeline.repository;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipeline.entity.PipelineType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PipelineRepository extends JpaRepository<Pipeline, Integer>
{
    Optional<Pipeline> findDefaultPipelineByOrganizationOrganizationIdAndPipelineType(Integer id, PipelineType type);
    List<Pipeline> findAllByOrganization(Organization org);
    Optional<Pipeline> findByIdAndOrganization(Integer id, Organization org);
    Optional<Pipeline> findByOrganizationAndPipelineTypeAndIsDefaultTrue(Organization org, PipelineType type);


}
