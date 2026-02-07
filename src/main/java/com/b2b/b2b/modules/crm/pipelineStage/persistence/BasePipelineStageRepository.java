package com.b2b.b2b.modules.crm.pipelineStage.persistence;

import com.b2b.b2b.modules.crm.pipeline.model.BasePipeline;
import com.b2b.b2b.modules.crm.pipelineStage.model.BasePipelineStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface BasePipelineStageRepository<S extends BasePipelineStage, P extends BasePipeline>
        extends JpaRepository<S, Integer>
{
    @Query("SELECT MAX(s.stageOrder) FROM #{#entityName} s WHERE s.pipeline.id =:pipelineId")
    Integer findMaxOrder(Integer pipelineId);
}
