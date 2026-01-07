package com.b2b.b2b.modules.crm.pipelineStage.service;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipeline.service.PipelineAssignable;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageRequestDTO;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageResponseDTO;

import java.util.List;
import java.util.Optional;

public interface PipelineStageService {
    List<PipelineStageResponseDTO> addPipelineStage(Integer id, List<PipelineStageRequestDTO> request, User user);
    List<PipelineStageResponseDTO> getAllPipelineStage(Integer id, User user);
    List<PipelineStageResponseDTO> updatePipelineStageById(Integer pipelineId, Integer stageId, PipelineStageRequestDTO request,  User user);

    Optional<PipelineStage> findNextPipelineStage(Pipeline  pipeline, Integer currentOrder);

    void promoteToNextStage(PipelineAssignable entity);
    List<PipelineStageResponseDTO>  deletePipelineStageById(Integer pipelineId, Integer stageId, Integer targetStageId, User user);
}
