package com.b2b.b2b.modules.crm.pipelineStage.service;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipeline.repository.PipelineRepository;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageRequestDTO;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageResponseDTO;
import com.b2b.b2b.modules.crm.pipelineStage.util.PipelineStageUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component("pipelineStageHelpers")
@RequiredArgsConstructor
class Helpers {

    private final PipelineRepository pipelineRepository;
    private final ModelMapper modelMapper;
    private final PipelineStageUtils pipelineStageUtils;

    List<PipelineStageResponseDTO> toDTOList(List<PipelineStage> stages) {
        return stages.stream().map(pipelineStageUtils::createPipelineStageResponseDTO).toList();
    }

    PipelineStage convertToEntity(PipelineStageRequestDTO request, Pipeline pipeline) {
        PipelineStage stage = modelMapper.map(request, PipelineStage.class);
        stage.setPipeline(pipeline);
        return stage;
    }

    void sortAndReindex(List<PipelineStage> existingStages) {
        //ordering/sorting stages and then inserting based on weight
        existingStages.sort(Comparator.comparingInt(PipelineStage::getStageOrder)
                .thenComparing(s -> s.getId() == null ? 1 : 0));

        //syncing the stage order in sequence 1,2,3.......
        reIndex(existingStages);
    }

    void reIndex(List<PipelineStage> existingStages) {
        for (int i = 0; i < existingStages.size(); i++) {
            existingStages.get(i).setStageOrder(i + 1);
        }
    }

    void validateMigrationTarget(Integer stageId, Integer targetStageId, long totalCount, List<PipelineStage> allStages) {
        if (targetStageId == null)
            throw new IllegalArgumentException(String.format("Stage contains %d records. You must provide a migration target stage.", totalCount));
        if (stageId.equals(targetStageId))
            throw new IllegalArgumentException("Target stage cannot be the same as the deleted stage.");

        boolean targetExists = allStages.stream().anyMatch(s -> s.getId().equals(targetStageId));

        if (!targetExists) throw new IllegalArgumentException("The target stage does not exist in this pipeline.");
    }

    Pipeline getPipeline(Integer pipelineId) {
        return pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline", "id", pipelineId));
    }

}
