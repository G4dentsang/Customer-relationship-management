package com.b2b.b2b.modules.crm.lead.repository;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LeadRepository extends JpaRepository<Lead, Integer> {

    List<Lead> findAllByAssignedUser(User user);
    Long countByPipeline(Pipeline pipeline);
    Long countByPipelineStage(PipelineStage pipelineStage);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Lead l SET l.pipeline.id = :targetId, l.pipelineStage.id = :stageId " +
            "WHERE l.pipeline.id = :sourceId ")
    void bulkMigration(Integer sourceId, Integer targetId, Integer stageId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Lead l SET l.pipelineStage.id = :targetId " +
            "WHERE l.pipelineStage.id = :sourceId")
    void bulkMigrateBetweenStage(Integer sourceId, Integer targetId);

    @Modifying
    @Query("UPDATE Lead l SET l.assignedUser.userId = :successorId " +
            "WHERE l.assignedUser.userId = :userId ")
    void reassignLeads(Integer userId, Integer successorId);
}
