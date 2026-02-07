package com.b2b.b2b.modules.crm.pipeline.service.impl;


import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.organization.model.Organization;
import com.b2b.b2b.modules.organization.persistence.OrganizationRepository;
import com.b2b.b2b.modules.crm.lead.model.Lead;
import com.b2b.b2b.modules.crm.lead.model.LeadStatus;
import com.b2b.b2b.modules.crm.lead.persistence.LeadRepository;
import com.b2b.b2b.modules.crm.pipeline.model.LeadPipeline;
import com.b2b.b2b.modules.crm.pipeline.payloads.CreatePipelineRequestDTO2;
import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineFilterDTO;
import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineMigrationRequestDTO;
import com.b2b.b2b.modules.crm.pipeline.persistence.LeadPipelineRepository;
import com.b2b.b2b.modules.crm.pipeline.service.LeadPipelineService;
import com.b2b.b2b.modules.crm.pipeline.util.PipelineSpecifications;
import com.b2b.b2b.modules.crm.pipeline.util.PipelineUtil;
import com.b2b.b2b.modules.crm.pipelineStage.model.LeadPipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.persistence.LeadPipelineStageRepository;
import com.b2b.b2b.modules.crm.pipelineStage.service.LeadPipelineStageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class LeadPipelineServiceImpl extends BasePipelineService<LeadPipeline, LeadPipelineStage> implements LeadPipelineService {

    private final LeadRepository leadRepository;
    private final LeadPipelineStageRepository leadPipelineStageRepository;

    public LeadPipelineServiceImpl(LeadPipelineRepository leadPipelineRepository,
                                   LeadPipelineStageService stageService,
                                   OrganizationRepository organizationRepository,
                                   PipelineUtil pipelineUtil,
                                   LeadRepository leadRepository,
                                   LeadPipelineStageRepository leadPipelineStageRepository) {
        super(leadPipelineRepository, stageService, organizationRepository, pipelineUtil);
        this.leadRepository = leadRepository;
        this.leadPipelineStageRepository = leadPipelineStageRepository;
    }

    @Override
    protected LeadPipeline createPipelineEntity(CreatePipelineRequestDTO2 request, Organization org) {
        return new LeadPipeline(request.getName(), request.isDefault(), org);
    }

    @Override
    protected Specification<LeadPipeline> getSearchSpecification(PipelineFilterDTO filter) {
        return PipelineSpecifications.createSearch(filter);
    }

    @Override
    protected long countAssociatedItems(LeadPipeline pipeline) {
        return leadRepository.countByPipeline(pipeline);
    }

    @Override
    public LeadPipeline assignDefaultPipeline(Lead lead) {
        LeadPipeline defaultPipeline = pipelineRepository.findByIsDefaultTrue()
                .orElseThrow(() -> new ResourceNotFoundException("Default Lead Pipeline", "leadId", lead.getId()));

      lead.setPipeline(defaultPipeline);
      return defaultPipeline;
    }

    @Override
    public LeadPipeline createDefaultPipeline(Organization org) {
        LeadPipeline pipeline = new LeadPipeline("Default Lead Pipeline", true, org);
        return pipelineRepository.save(pipeline);
    }

    @Override
    protected void transferData(LeadPipeline source, LeadPipeline target, PipelineMigrationRequestDTO request) {
        request.getStageMapping().forEach((oldStageId, newStageId) -> {

            leadRepository.bulkMoveLeads(
                    source.getId(),
                    oldStageId,
                    target.getId(),
                    newStageId
            );
            log.info("Moved leads from Old Stage {} to New Stage {}", oldStageId, newStageId);
        });
    }

    @Override
    protected List<Integer> getStageIdsWithData(LeadPipeline source) {
        // getting stage id's with leads in this pipeline
        return leadRepository.findStageIdsWithLeads(source.getId());
    }

    @Override
    protected void validateStatusMove(Integer sourceStageId, Integer targetStageId) {
        LeadPipelineStage source = leadPipelineStageRepository.findById(sourceStageId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead Pipeline Stage", "stageId", sourceStageId));
        LeadPipelineStage target = leadPipelineStageRepository.findById(targetStageId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead Pipeline Stage", "stageId", targetStageId));

        if (source.getMappedStatus() == LeadStatus.CONVERTED && target.getMappedStatus() != LeadStatus.CONVERTED) {
            throw new IllegalArgumentException(
                    String.format("Illegal Move: Cannot move CONVERTED leads (Stage: %s) to a non-converted stage (Stage: %s).",
                            source.getStageName(), target.getStageName()));
        }
        if (source.getMappedStatus() == LeadStatus.LOST && target.getMappedStatus() != LeadStatus.LOST) {
            throw new IllegalArgumentException(
                    String.format("Illegal Move: Cannot move LOST leads (Stage: %s) to a non-lost stage (Stage: %s).",
                            source.getStageName(), target.getStageName()));
        }
    }

}
