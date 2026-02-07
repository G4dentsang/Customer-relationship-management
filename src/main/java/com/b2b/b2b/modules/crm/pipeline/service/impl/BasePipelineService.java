package com.b2b.b2b.modules.crm.pipeline.service.impl;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.repository.OrganizationRepository;
import com.b2b.b2b.modules.crm.pipeline.entity.BasePipeline;
import com.b2b.b2b.modules.crm.pipeline.payloads.*;
import com.b2b.b2b.modules.crm.pipeline.repository.BasePipelineRepository;
import com.b2b.b2b.modules.crm.pipeline.service.PipelineOperations;
import com.b2b.b2b.modules.crm.pipeline.util.PipelineUtil;
import com.b2b.b2b.modules.crm.pipelineStage.service.PipelineStageOperations;
import com.b2b.b2b.shared.multitenancy.OrganizationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public abstract class BasePipelineService<P extends BasePipeline> implements PipelineOperations<P> {
    protected final BasePipelineRepository<P> pipelineRepository;
    protected final PipelineStageOperations<P, ?> stageService;
    protected final OrganizationRepository organizationRepository;
    protected final PipelineUtil pipelineUtil;

    @Override
    @Transactional
    public PipelineResponseDTO createPipeline(CreatePipelineRequestDTO2 request) {
        Organization org = organizationRepository.findById(OrganizationContext.getOrgId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", OrganizationContext.getOrgId()));
        P pipeline = createPipelineEntity(request, org);

        if (request.getStages() != null && !request.getStages().isEmpty()) {
            stageService.addStage(pipeline.getId(), request.getStages());
        } else {
            stageService.createDefaultStages(pipeline);
        }

        return pipelineUtil.createPipelineResponseDTO(pipeline);
    }

    @Override
    public Page<PipelineResponseDTO> getAllPipeline(PipelineFilterDTO filter, Pageable pageable) {
        Specification<P> spec = getSearchSpecification(filter);
        return pipelineRepository.findAll(spec, pageable)
                .map(pipelineUtil::createPipelineResponseDTO);
    }

    @Override
    public PipelineResponseDTO getPipeline(Integer pipelineId) {
        P pipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(()-> new ResourceNotFoundException("Pipeline", "id", pipelineId));
        return pipelineUtil.createPipelineResponseDTO(pipeline);
    }

    @Override
    @Transactional
    public PipelineResponseDTO updatePipelineById(Integer id, UpdatePipelineRequestDTO request) {
        P pipeline = pipelineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline", "id", id));

        if (request.isDefault() && !pipeline.isDefault()) {
            pipelineRepository.findByIsDefaultTrue()
                    .ifPresent(oldDefault -> {
                        if (!oldDefault.getId().equals(pipeline.getId())) {
                            oldDefault.setDefault(false);
                            pipelineRepository.save(oldDefault);
                        }
                    });
        }
        pipeline.setPipelineName(request.getPipelineName());
        pipeline.setDefault(request.isDefault());

        return pipelineUtil.createPipelineResponseDTO(pipelineRepository.save(pipeline));
    }

    @Override
    @Transactional
    public void inactivatePipeline(Integer pipelineId) {
        P pipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline", "id", pipelineId));

        if (pipeline.isDefault()) throw new IllegalArgumentException("Cannot inactivate the default pipeline.");

        long count = countAssociatedItems(pipeline);

        if (count > 0) throw new IllegalArgumentException((
                    String.format("Pipeline contains %d items. Migrate them to another pipeline before inactivating.", count)));

        pipeline.setActive(false);
        pipelineRepository.save(pipeline);

    }

    @Override
    @Transactional
    public void migrateAndInactivate(Integer sourceId, PipelineMigrationRequestDTO request) {
        P source = pipelineRepository.findById(sourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Source Pipeline", "id", sourceId));
        P target = pipelineRepository.findById(request.getTargetPipelineId())
                .orElseThrow(() -> new ResourceNotFoundException("Target Pipeline", "id", request.getTargetPipelineId()));

        validateMigration( source, target, request);
        transferData(source, target, request);

        source.setActive(false);
        source.setDefault(false);
        pipelineRepository.save(source);

        log.info("Pipeline {} inactivated after migrating data to {}", source.getId(), target.getId());
    }

    private void validateMigration(P source, P target, PipelineMigrationRequestDTO request) {
        if (source.isDefault()) throw new IllegalArgumentException("Cannot inactivate the default pipeline.");

        if (!target.isActive())throw new IllegalArgumentException("Target pipeline must be active to receive migrated records.");

        if (source.getId().equals(target.getId())) throw new IllegalArgumentException("Cannot migrate to self.");

        List<Integer> stagesWithData = getStageIdsWithData(source);

        List<Integer> unmappedStages = stagesWithData.stream().filter(id -> !request.getStageMapping().containsKey(id)).toList();

        if (!unmappedStages.isEmpty()) throw new IllegalArgumentException("Data Integrity Error: You have items in Source Stages " + unmappedStages + " but provided no target mapping for them.");

        request.getStageMapping().forEach((sourceStageId, targetStageId) -> {
            validateStatusMove(sourceStageId, targetStageId);
        });
    }

    protected abstract P createPipelineEntity(CreatePipelineRequestDTO2 request, Organization org);

    protected abstract Specification<P> getSearchSpecification(PipelineFilterDTO filter);

    protected abstract long countAssociatedItems(P pipeline);

    protected abstract void transferData(P source, P target, PipelineMigrationRequestDTO request);

    protected abstract List<Integer> getStageIdsWithData(P source);

    protected abstract void validateStatusMove(Integer sourceStageId, Integer targetStageId);

}
