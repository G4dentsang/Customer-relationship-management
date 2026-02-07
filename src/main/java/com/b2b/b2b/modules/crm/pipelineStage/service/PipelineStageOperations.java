package com.b2b.b2b.modules.crm.pipelineStage.service;

import com.b2b.b2b.modules.crm.pipeline.entity.BasePipeline;
import com.b2b.b2b.modules.crm.pipelineStage.entity.BasePipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageRequestDTO;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageResponseDTO;

import java.util.List;

public interface PipelineStageOperations <P extends BasePipeline, S extends BasePipelineStage> {

    List<PipelineStageResponseDTO> addStage(Integer pipelineId, List<PipelineStageRequestDTO> request);

    List<PipelineStageResponseDTO> updateStage(Integer pipelineId, Integer stageId, PipelineStageRequestDTO request);

    List<PipelineStageResponseDTO>  deleteStage(Integer pipelineId, Integer stageId, Integer targetStageId);

    List<PipelineStageResponseDTO> getAllStages(Integer pipelineId);

    void createDefaultStages(P pipeline);

    //void promoteToNextStage(I entity);
}
