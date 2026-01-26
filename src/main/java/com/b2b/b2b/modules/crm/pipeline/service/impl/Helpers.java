package com.b2b.b2b.modules.crm.pipeline.service.impl;

import com.b2b.b2b.exception.APIException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipeline.payloads.CreatePipelineRequestDTO;
import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineResponseDTO;
import com.b2b.b2b.modules.crm.pipeline.util.PipelineUtil;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageRequestDTO;
import com.b2b.b2b.modules.crm.pipelineStage.repository.PipelineStageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
class Helpers {

    private final PipelineUtil pipelineUtil;
    private final PipelineStageRepository pipelineStageRepository;

    Page<PipelineResponseDTO> toDTOList(Page<Pipeline> pipelines) {
        return pipelines.map(pipelineUtil::createPipelineResponseDTO);
    }

    Pipeline convertToEntity(CreatePipelineRequestDTO request, Organization org) {
        Pipeline pipeline = new Pipeline();
        pipeline.setPipelineName(request.getPipelineName());
        pipeline.setPipelineType(request.getPipelineType());
        pipeline.setDefault(request.isDefault());
        pipeline.setOrganization(org);
        return pipeline;
    }

    boolean hasCustomStages(CreatePipelineRequestDTO request) {
        return request.getStages() != null && !request.getStages().isEmpty();
    }

    void createCustomStages(Pipeline pipeline, List<PipelineStageRequestDTO> stagesDTOs) {
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

    void createDefaultStage(Pipeline pipeline) {
        PipelineStage pipelineStage = new PipelineStage();
        pipelineStage.setStageName("Default Stage");
        pipelineStage.setStageOrder(0);
        pipelineStage.setPipeline(pipeline);
        pipelineStageRepository.save(pipelineStage);
    }

    PipelineStage findFirstStage(Pipeline pipeline) {
        return pipeline.getPipelineStages()
                .stream()
                .min(Comparator.comparingInt(PipelineStage::getStageOrder))
                .orElseThrow(() -> new APIException("Pipeline " + pipeline.getPipelineName() + " has no stage defined"));
    }
}
