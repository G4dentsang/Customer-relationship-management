package com.b2b.b2b.modules.crm.pipeline.service;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.pipeline.entity.PipelineType;
import com.b2b.b2b.modules.crm.pipeline.payloads.CreatePipelineRequestDTO;
import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineResponseDTO;

import java.util.List;


public interface PipelineService {

    <T extends PipelineAssignable> void assignDefaultPipeline(T entity, PipelineType pipelineType);
    PipelineResponseDTO createPipeline(CreatePipelineRequestDTO createPipelineRequestDTO, User user);
    List<PipelineResponseDTO> getAllPipeline(User user);
    PipelineResponseDTO getPipelineById(Integer pipelineId,User user);
}
