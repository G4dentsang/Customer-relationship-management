package com.b2b.b2b.modules.crm.deal.repository;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface DealRepository extends JpaRepository<Deal, Integer>, JpaSpecificationExecutor<Deal> {

    Page<Deal> findAllByAssignedUser(User user, Pageable pageable);
    Page<Deal> findAllByCompanyId(Integer id, Pageable pageable);
    Page<Deal> findAllByCompanyContactsId(Integer id, Pageable pageable);

    Long countByPipeline(Pipeline pipeline);
    Long countByPipelineStage(PipelineStage pipelineStage);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Deal d SET d.pipeline.id = :targetId, d.pipelineStage.id = :stageId " +
            "WHERE d.pipeline.id = :sourceId ")
    void bulkMigration(Integer sourceId, Integer  targetId, Integer stageId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Deal d SET d.pipelineStage.id = :targetId " +
            "WHERE d.pipelineStage.id = :sourceId")
    void bulkMigrateBetweenStage(Integer sourceId, Integer targetId);

    @Modifying
    @Query("UPDATE Deal d SET d.assignedUser.userId = :successorId " +
            "WHERE d.assignedUser.userId = :userId")
    void reassignDeals(Integer userId, Integer successorId);
}

