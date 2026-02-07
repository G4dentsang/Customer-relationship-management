package com.b2b.b2b.modules.crm.deal.repository;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.pipeline.entity.DealPipeline;
import com.b2b.b2b.modules.crm.pipelineStage.entity.DealPipelineStage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface DealRepository extends JpaRepository<Deal, Integer>, JpaSpecificationExecutor<Deal> {

    Page<Deal> findAllByAssignedUser(User user, Pageable pageable);
    Page<Deal> findAllByCompanyId(Integer id, Pageable pageable);
    Page<Deal> findAllByCompanyContactsId(Integer id, Pageable pageable);


    Long countByPipeline(DealPipeline pipeline);
    Long countByPipelineStage(DealPipelineStage pipelineStage);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Deal d
            SET d.pipeline.id = :targetPipelineId,
                d.pipelineStage.id = :targetStageId
            WHERE d.pipeline.id = :sourcePipelineId
              AND d.pipelineStage.id = :soruceStageId
            """)
    void bulkMoveDeals(Integer sourcePipelineId, Integer sourceStageId,
                       Integer targetPipelineId, Integer targetStageId);

    @Query("SELECT DISTINCT d.pipelineStage.id " +
            "FROM Deal d " +
            "WHERE d.pipeline.id = :pipelineId")
    List<Integer> findStageIdsWithDeals(Integer pipelineId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Deal d " +
            "SET d.pipelineStage.id = :targetId " +
            "WHERE d.pipelineStage.id = :sourceId")
    void bulkMigrateBetweenStage(Integer sourceId, Integer targetId);

    @Modifying
    @Query("UPDATE Deal d " +
            "SET d.assignedUser.userId = :successorId " +
            "WHERE d.assignedUser.userId = :userId")
    void reassignDeals(Integer userId, Integer successorId);
}

