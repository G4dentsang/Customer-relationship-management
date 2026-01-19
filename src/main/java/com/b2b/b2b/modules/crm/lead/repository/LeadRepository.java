package com.b2b.b2b.modules.crm.lead.repository;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LeadRepository extends JpaRepository<Lead, Integer> {
    List<Lead> findAllByOrganization(Organization organization);
    List<Lead> findAllByOwnerAndOrganization(User user, Organization org);

    Optional<Lead> findByIdAndOrganization(Integer id, Organization org);
    Long countByPipeline(Pipeline pipeline);
    Long countByPipelineStage(PipelineStage pipelineStage);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Lead l SET l.pipeline.id = :targetId, l.pipelineStage.id = :stageId " +
            "WHERE l.pipeline.id = :sourceId AND l.organization.organizationId =:orgId")
    void bulkMigration(Integer sourceId, Integer  targetId, Integer stageId, Integer orgId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Lead l SET l.pipelineStage.id = :targetId " +
            "WHERE l.pipelineStage.id = :sourceId AND l.organization.organizationId =:orgId")
    void bulkMigrateBetweenStage(Integer sourceId, Integer targetId, Integer orgId);

    @Modifying
    @Query("UPDATE Lead l SET l.assignedUser.userId = :successorId " +
            "WHERE l.assignedUser.userId = :userId AND l.organization.organizationId =:orgId")
    void reassignLeads(Integer userId, Integer successorId, Integer orgID);
}
