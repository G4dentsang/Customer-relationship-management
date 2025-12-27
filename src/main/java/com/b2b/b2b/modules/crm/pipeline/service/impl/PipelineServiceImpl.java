package com.b2b.b2b.modules.crm.pipeline.service.impl;

import com.b2b.b2b.exception.APIException;
import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipeline.entity.PipelineType;
import com.b2b.b2b.modules.crm.pipeline.payloads.CreatePipelineRequestDTO;
import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineResponseDTO;
import com.b2b.b2b.modules.crm.pipeline.repository.PipelineRepository;
import com.b2b.b2b.modules.crm.pipeline.service.PipelineAssignable;
import com.b2b.b2b.modules.crm.pipeline.service.PipelineService;
import com.b2b.b2b.modules.crm.pipeline.util.PipelineUtil;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageRequestDTO;
import com.b2b.b2b.modules.crm.pipelineStage.repository.PipelineStageRepository;
import com.b2b.b2b.shared.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PipelineServiceImpl implements PipelineService {

    private final PipelineRepository pipelineRepository;
    private final PipelineStageRepository pipelineStageRepository;
    private final PipelineUtil pipelineUtil;
    private final AuthUtil authUtil;

    @Override
    @Transactional
    public PipelineResponseDTO createPipeline(CreatePipelineRequestDTO request, User user) {
        Organization org = getOrg(user);
        Pipeline pipeline = pipelineRepository.save(convertToEntity(request, org));

        if (hasCustomStages(request)) {
            createCustomStages(pipeline, request.getStages());
        } else {
            createDefaultStage(pipeline);
        }

        return pipelineUtil.createPipelineResponseDTO(pipeline);
    }

    @Override
    public List<PipelineResponseDTO> getAllPipeline( User user) {
        return toDTOList(pipelineRepository.findAllByOrganization(getOrg(user)));
    }

    @Override
    public PipelineResponseDTO getPipelineById(Integer pipelineId, User user) {
        Pipeline pipeline = pipelineRepository.findByIdAndOrganization(pipelineId, getOrg(user))
                .orElseThrow(()-> new ResourceNotFoundException("Pipeline", "id", pipelineId));
        return pipelineUtil.createPipelineResponseDTO(pipeline);
    }

    @Override
    @Transactional(readOnly = true)
    public <T extends PipelineAssignable> void assignDefaultPipeline(T entity, PipelineType type) {
        Integer orgId = entity.getOrganization().getOrganizationId();
        Pipeline pipeline = pipelineRepository.findDefaultPipelineByOrganizationOrganizationIdAndPipelineType(orgId, type)
                .orElseThrow(() -> new ResourceNotFoundException("Default Pipeline ", "type", type.name()));

        PipelineStage pipelineStage = findFirstStage(pipeline);

        entity.setPipeline(pipeline);
        entity.setPipelineStage(pipelineStage);
    }

    /********Helper methods********/

    private Organization getOrg(User user){
        return authUtil.getPrimaryOrganization(user);
    }

    private List<PipelineResponseDTO> toDTOList(List<Pipeline> pipelines){
        return pipelines.stream().map(pipelineUtil::createPipelineResponseDTO).toList();
    }

    private Pipeline convertToEntity(CreatePipelineRequestDTO request, Organization org) {
        Pipeline pipeline = new Pipeline();
        pipeline.setPipelineName(request.getPipelineName());
        pipeline.setPipelineType(request.getPipelineType());
        pipeline.setDefault(request.isDefault());
        pipeline.setOrganization(org);
        return pipeline;
    }

    private boolean hasCustomStages(CreatePipelineRequestDTO request) {
        return request.getStages() != null && !request.getStages().isEmpty();
    }

    private void createCustomStages(Pipeline pipeline, List<PipelineStageRequestDTO> stagesDTOs) {
        List<PipelineStage> stages = new ArrayList<>();

        for (int i = 0; i < stagesDTOs.size(); i++) {
            PipelineStageRequestDTO dto = stagesDTOs.get(i);
            PipelineStage pipelineStage = new PipelineStage();
            pipelineStage.setStageName(dto.getStageName());
            pipelineStage.setStageDescription(dto.getStageDescription());
            pipelineStage.setPipeline(pipeline);

            int stageOder = (dto.getStageOrder() != null) ? dto.getStageOrder() : i;
            pipelineStage.setStageOrder(stageOder);

            stages.add(pipelineStage);
        }
        pipelineStageRepository.saveAll(stages);
    }

    public void createDefaultStage(Pipeline pipeline) {
        PipelineStage pipelineStage = new PipelineStage();
        pipelineStage.setStageName("Default Stage");
        pipelineStage.setStageOrder(0);
        pipelineStage.setPipeline(pipeline);
        pipelineStageRepository.save(pipelineStage);
    }

    private PipelineStage findFirstStage(Pipeline pipeline) {
        return pipeline.getPipelineStages()
                .stream()
                .min(Comparator.comparingInt(PipelineStage::getStageOrder))
                .orElseThrow(()-> new APIException("Pipeline " + pipeline.getPipelineName() + " has no stage defined"));
    }
}
