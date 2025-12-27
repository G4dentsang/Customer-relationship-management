package com.b2b.b2b.modules.crm.pipelineStage.repository;

import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PipelineStageRepository extends JpaRepository<PipelineStage, Integer>
{
    @Query("SELECT ps FROM PipelineStage ps WHERE ps.pipeline = :pipeline AND ps.stageOrder > :currentOrder ORDER BY ps.stageOrder ASC LIMIT 1")
    Optional<PipelineStage> findNextStages(@Param("pipeline") Pipeline pipeline, @Param("currentOrder") Integer currentOrder);
    List<PipelineStage> findAllByPipelineOrderByStageOrderAsc(Pipeline pipeline);

}
