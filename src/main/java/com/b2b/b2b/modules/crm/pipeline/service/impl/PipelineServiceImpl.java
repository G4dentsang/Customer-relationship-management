package com.b2b.b2b.modules.crm.pipeline.service.impl;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.repository.OrganizationRepository;
import com.b2b.b2b.modules.crm.deal.repository.DealRepository;
import com.b2b.b2b.modules.crm.lead.repository.LeadRepository;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipeline.entity.PipelineType;
import com.b2b.b2b.modules.crm.pipeline.payloads.CreatePipelineRequestDTO;
import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineResponseDTO;
import com.b2b.b2b.modules.crm.pipeline.payloads.UpdatePipelineRequestDTO;
import com.b2b.b2b.modules.crm.pipeline.repository.PipelineRepository;
import com.b2b.b2b.modules.crm.pipeline.service.PipelineAssignable;
import com.b2b.b2b.modules.crm.pipeline.service.PipelineService;
import com.b2b.b2b.modules.crm.pipeline.util.PipelineUtil;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import com.b2b.b2b.shared.multitenancy.OrganizationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PipelineServiceImpl implements PipelineService {

    private final PipelineRepository pipelineRepository;
    private final PipelineUtil pipelineUtil;
    private final LeadRepository leadRepository;
    private final DealRepository dealRepository;
    private final OrganizationRepository organizationRepository;
    private final Helpers helpers;

    @Override
    @Transactional
    public PipelineResponseDTO createPipeline(CreatePipelineRequestDTO request) {
        Organization org = organizationRepository.findById(OrganizationContext.getOrgId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", OrganizationContext.getOrgId()));
        Pipeline pipeline = pipelineRepository.save(helpers.convertToEntity(request, org));

        if (helpers.hasCustomStages(request)) {
            helpers.createCustomStages(pipeline, request.getStages());
        } else {
            helpers.createDefaultStage(pipeline);
        }

        return pipelineUtil.createPipelineResponseDTO(pipeline);
    }

    @Override
    public List<PipelineResponseDTO> getAllPipeline() {
        return helpers.toDTOList(pipelineRepository.findAll());
    }

    @Override
    public PipelineResponseDTO getPipelineById(Integer pipelineId) {
        Pipeline pipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(()-> new ResourceNotFoundException("Pipeline", "id", pipelineId));
        return pipelineUtil.createPipelineResponseDTO(pipeline);
    }

    @Override
    @Transactional
    public PipelineResponseDTO updatePipelineById(Integer id, UpdatePipelineRequestDTO request) {
        Pipeline pipeline = pipelineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline", "id", id));

        if (request.isDefault() && !pipeline.isDefault()) {
            pipelineRepository.findByPipelineTypeAndIsDefaultTrue(pipeline.getPipelineType())
                    .ifPresent(oldDefault -> {
                        if (!oldDefault.getId().equals(pipeline.getId())) {
                            oldDefault.setDefault(false);
                            pipelineRepository.save(oldDefault);
                            log.info("Pipeline {} is no longer the default {} pipeline for Org {}",
                                    oldDefault.getId(), pipeline.getPipelineType(), OrganizationContext.getOrgId());
                        }
                    });
        }
        pipeline.setPipelineName(request.getPipelineName());
        pipeline.setDefault(request.isDefault());

        return pipelineUtil.createPipelineResponseDTO(pipelineRepository.save(pipeline));
    }

    @Override
    @Transactional
    public void inactivatePipelineById(Integer id) {
        Pipeline pipeline = pipelineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline", "id", id));

        if (pipeline.isDefault()) {
            throw new IllegalArgumentException("Cannot inactivate the default pipeline.");
        }

        long leadCount = leadRepository.countByPipeline(pipeline);
        long dealCount = dealRepository.countByPipeline(pipeline);

        if (leadCount > 0 || dealCount > 0) {
            throw new IllegalArgumentException((
                    String.format("Pipeline contains %d leads and %d deals. Migrate them to another pipeline before inactivating.", leadCount, dealCount)
            ));
        }
        pipeline.setActive(false);
        pipelineRepository.save(pipeline);

    }

    @Override
    @Transactional(readOnly = true)
    public <T extends PipelineAssignable> void assignDefaultPipeline(T entity, PipelineType type) {
        Pipeline pipeline = pipelineRepository.findByPipelineTypeAndIsDefaultTrue(type)
                .orElseThrow(() -> new ResourceNotFoundException("Default Pipeline ", "type", type.name()));

        PipelineStage pipelineStage = helpers.findFirstStage(pipeline);

        entity.setPipeline(pipeline);
        entity.setPipelineStage(pipelineStage);
    }
}
