package com.b2b.b2b.modules.crm.pipeline.persistence;

import com.b2b.b2b.modules.crm.pipeline.model.BasePipeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;
@NoRepositoryBean
public interface BasePipelineRepository<P extends BasePipeline> extends JpaRepository<P , Integer>, JpaSpecificationExecutor<P>
{
    Optional<P> findByIsDefaultTrue();
}
