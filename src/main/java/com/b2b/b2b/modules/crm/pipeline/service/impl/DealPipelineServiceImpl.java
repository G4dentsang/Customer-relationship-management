package com.b2b.b2b.modules.crm.pipeline.service.impl;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.organization.model.Organization;
import com.b2b.b2b.modules.organization.persistence.OrganizationRepository;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.persistence.Deal;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.persistence.DealStatus;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.persistence.DealRepository;
import com.b2b.b2b.modules.crm.pipeline.model.DealPipeline;
import com.b2b.b2b.modules.crm.pipeline.payloads.CreatePipelineRequestDTO2;
import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineFilterDTO;
import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineMigrationRequestDTO;
import com.b2b.b2b.modules.crm.pipeline.persistence.DealPipelineRepository;
import com.b2b.b2b.modules.crm.pipeline.service.DealPipelineService;
import com.b2b.b2b.modules.crm.pipeline.util.PipelineSpecifications;
import com.b2b.b2b.modules.crm.pipeline.util.PipelineUtil;
import com.b2b.b2b.modules.crm.pipelineStage.model.DealPipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.persistence.DealPipelineStageRepository;
import com.b2b.b2b.modules.crm.pipelineStage.service.DealPipelineStageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class DealPipelineServiceImpl extends BasePipelineService<DealPipeline, DealPipelineStage> implements DealPipelineService {
    private final DealRepository dealRepository;
    private final DealPipelineStageRepository dealPipelineStageRepository;

    public DealPipelineServiceImpl(DealPipelineRepository dealPipelineRepository,
                                   DealPipelineStageService stageService,
                                   OrganizationRepository organizationRepository,
                                   PipelineUtil pipelineUtil,
                                   DealRepository dealRepository,
                                   DealPipelineStageRepository dealPipelineStageRepository) {
        super(dealPipelineRepository, stageService, organizationRepository, pipelineUtil);
        this.dealRepository = dealRepository;
        this.dealPipelineStageRepository = dealPipelineStageRepository;
    }

    @Override
    protected DealPipeline createPipelineEntity(CreatePipelineRequestDTO2 request, Organization org) {
        return new DealPipeline(request.getName(), request.isDefault(), org);
    }

    @Override
    protected Specification<DealPipeline> getSearchSpecification(PipelineFilterDTO filter) {
        return PipelineSpecifications.createSearch(filter);
    }

    @Override
    protected long countAssociatedItems(DealPipeline pipeline) {
        return dealRepository.countByPipeline(pipeline);
    }

    @Override
    public DealPipeline assignDefaultPipeline(Deal deal) {
        DealPipeline defaultPipeline = pipelineRepository.findByIsDefaultTrue()
                .orElseThrow(() -> new ResourceNotFoundException("Default Deal Pipeline", "dealID", deal.getId()));

        deal.setPipeline(defaultPipeline);
        return defaultPipeline;
    }

    @Override
    public DealPipeline createDefaultPipeline(Organization org) {
        DealPipeline pipeline = new DealPipeline("Default Deal Pipeline", true, org);
        return pipelineRepository.save(pipeline);
    }

    @Override
    protected void transferData(DealPipeline source, DealPipeline target, PipelineMigrationRequestDTO request) {
        request.getStageMapping().forEach((oldStageId, newStageId) -> {

            dealRepository.bulkMoveDeals(
                    source.getId(),
                    oldStageId,
                    target.getId(),
                    newStageId
            );
            log.info("Moved deals from Old Stage {} to New Stage {}", oldStageId, newStageId);
        });

    }

    @Override
    protected List<Integer> getStageIdsWithData(DealPipeline source) {
        return dealRepository.findStageIdsWithDeals(source.getId());
    }

    @Override
    protected void validateStatusMove(Integer sourceStageId, Integer targetStageId) {
        DealPipelineStage source = dealPipelineStageRepository.findById(sourceStageId)
                .orElseThrow(() -> new ResourceNotFoundException("Deal Pipeline Stage", "stageId", sourceStageId));
        DealPipelineStage target = dealPipelineStageRepository.findById(targetStageId)
                .orElseThrow(() -> new ResourceNotFoundException("Deal Pipeline Stage", "stageId", targetStageId));

        if (source.getMappedStatus() == DealStatus.CLOSED_WON && target.getMappedStatus() != DealStatus.CLOSED_WON) {
            throw new IllegalArgumentException(
                    String.format("Illegal Move: Cannot move WON deals (Stage: %s) to a non-won stage (Stage: %s).",
                            source.getStageName(), target.getStageName()));
        }
        if (source.getMappedStatus() == DealStatus.CLOSED_LOST && target.getMappedStatus() != DealStatus.CLOSED_LOST) {
            throw new IllegalArgumentException(
                    String.format("Illegal Move: Cannot move LOST deals (Stage: %s) to a non-lost stage (Stage: %s).",
                            source.getStageName(), target.getStageName()));
        }
    }
}
