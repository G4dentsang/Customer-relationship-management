package com.b2b.b2b.modules.crm.pipelineStage.service;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageRequestDTO;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageResponseDTO;

import java.util.List;

public interface PipelineStageService {
    PipelineStage findNextPipelineStage(Pipeline  pipeline, Integer currentOrder);
    List<PipelineStageResponseDTO> addPipelineStage(Integer pipelineId, List<PipelineStageRequestDTO> pipelineStageRequestDTOs, User user);
    List<PipelineStageResponseDTO> getPipelineStage(Integer pipelineId, User user);
}
