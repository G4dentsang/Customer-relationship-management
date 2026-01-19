package com.b2b.b2b.modules.crm.deal.repository;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DealRepository extends JpaRepository<Deal, Integer> {
    List<Deal> findAllByOrganization(Organization org);
    List<Deal> findAllByOwnerAndOrganization(User user, Organization org);

    List<Deal> findAllDealsByCompanyIdAndOrganization(Integer id, Organization org);
    List<Deal> findAllDealsByCompanyContactsIdAndOrganization(Integer id, Organization org);

    Optional<Deal> findByIdAndOrganization(Integer id, Organization org);

    Long countByPipeline(Pipeline pipeline);
    Long countByPipelineStage(PipelineStage pipelineStage);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Deal d SET d.pipeline.id = :targetId, d.pipelineStage.id = :stageId " +
            "WHERE d.pipeline.id = :sourceId AND d.organization.organizationId =:orgId")
    void bulkMigration(Integer sourceId, Integer  targetId, Integer stageId, Integer orgId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Deal d SET d.pipelineStage.id = :targetId " +
            "WHERE d.pipelineStage.id = :sourceId AND d.organization.organizationId =:orgId")
    void bulkMigrateBetweenStage(Integer sourceId, Integer targetId, Integer orgId);

    @Modifying
    @Query("UPDATE Deal d SET d.assignedUser.userId = :successorId " +
            "WHERE d.assignedUser.userId = :userId AND d.organization.organizationId =:orgId")
    void reassignDeals(Integer userId, Integer successorId, Integer orgID);
}

