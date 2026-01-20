package com.b2b.b2b.modules.crm.pipeline.service.impl;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.crm.deal.repository.DealRepository;
import com.b2b.b2b.modules.crm.lead.repository.LeadRepository;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipeline.entity.PipelineType;
import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineMigrationRequestDTO;
import com.b2b.b2b.modules.crm.pipeline.repository.PipelineRepository;
import com.b2b.b2b.modules.crm.pipeline.service.MigrationService;
import com.b2b.b2b.shared.multitenancy.OrganizationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MigrationServiceImpl implements MigrationService
{
    private final PipelineRepository pipelineRepository;
    private final LeadRepository leadRepository;
    private final DealRepository dealRepository;

    @Override
    @Transactional
    public void migrateAndInactivate(Integer sourceId, PipelineMigrationRequestDTO request) {

        Pipeline source = pipelineRepository.findById(sourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Source Pipeline", "id", sourceId));
        Pipeline target = pipelineRepository.findById(request.getTargetPipelineId())
                .orElseThrow(() -> new ResourceNotFoundException("Target Pipeline", "id", request.getTargetPipelineId()));

        validateMigration(request, source, target);

        if (source.getPipelineType() == PipelineType.LEAD) {
            leadRepository.bulkMigration(sourceId, target.getId(), request.getTargetStageId());
        } else {
            dealRepository.bulkMigration(sourceId, target.getId(), request.getTargetStageId());
        }

        source.setActive(false);
        source.setDefault(false);
        pipelineRepository.save(source);

    }

    private static void validateMigration(PipelineMigrationRequestDTO request, Pipeline source, Pipeline target) {
        if (source.isDefault()) {
            throw new IllegalArgumentException("Cannot inactivate the default pipeline.");
        }
        if(source.getPipelineType() != target.getPipelineType()) {
            throw new IllegalArgumentException("Mismatched Pipeline Types: Cannot migrate" + source.getPipelineType() + " to " + target.getPipelineType());
        }
        if(!target.isActive()) {
            throw new IllegalArgumentException("Target pipeline must be active to receive migrated records.");
        }

        boolean stageBelongsToTarget = target.getPipelineStages().stream()
                .anyMatch(stage -> stage.getId().equals(request.getTargetStageId()));

        if(!stageBelongsToTarget) {
            throw new IllegalArgumentException("The selected landing stage does not exist in the target pipeline.");
        }
    }
}
