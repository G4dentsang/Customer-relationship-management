package com.b2b.b2b.modules.crm.lead.persistence;

import com.b2b.b2b.modules.crm.lead.model.Lead;
import com.b2b.b2b.modules.crm.pipeline.model.LeadPipeline;
import com.b2b.b2b.modules.crm.pipelineStage.model.LeadPipelineStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface LeadRepository extends JpaRepository<Lead, Integer>, JpaSpecificationExecutor<Lead> {

    Long countByPipeline(LeadPipeline pipeline);

    Long countByPipelineStage(LeadPipelineStage pipelineStage);

    @Modifying(clearAutomatically = true)
    @Query(""" 
            UPDATE Lead l
            SET l.pipeline.id = :targetPipelineId,
                l.pipelineStage.id = :targetStageId
            WHERE l.pipeline.id = :sourcePipelineId
              AND l.pipelineStage.id = : sourceStageId
            """)
    void bulkMoveLeads(Integer sourcePipelineId, Integer sourceStageId,
                       Integer targetPipelineId, Integer targetStageId);

    @Query("SELECT DISTINCT l.pipelineStage.id FROM Lead l WHERE l.pipeline.id = :pipelineId")
    List<Integer> findStageIdsWithLeads(Integer pipelineId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Lead l SET l.pipelineStage.id = :targetId " +
            "WHERE l.pipelineStage.id = :sourceId")
    void bulkMigrateBetweenStage(Integer sourceId, Integer targetId);

    @Modifying
    @Query("UPDATE Lead l SET l.assignedUser.userId = :successorId " +
            "WHERE l.assignedUser.userId = :userId ")
    void reassignLeads(Integer userId, Integer successorId);
}
