package com.b2b.b2b.modules.crm.pipelineStage.service;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageRequestDTO;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageResponseDTO;

import java.util.List;
import java.util.Optional;

public interface PipelineStageService {
    Optional<PipelineStage> findNextPipelineStage(Pipeline  pipeline, Integer currentOrder);
    void promoteToNextStage(Lead lead);
    List<PipelineStageResponseDTO> addPipelineStage(Integer id, List<PipelineStageRequestDTO> request, User user);
    List<PipelineStageResponseDTO> getPipelineStage(Integer id, User user);
}
