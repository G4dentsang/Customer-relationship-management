package com.b2b.b2b.modules.crm.pipeline.service;

import com.b2b.b2b.modules.crm.pipeline.entity.PipelineType;
import com.b2b.b2b.modules.crm.pipeline.payloads.CreatePipelineRequestDTO;
import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineResponseDTO;
import com.b2b.b2b.modules.crm.pipeline.payloads.UpdatePipelineRequestDTO;

import java.util.List;


public interface PipelineService {

    <T extends PipelineAssignable> void assignDefaultPipeline(T entity, PipelineType type);
    PipelineResponseDTO createPipeline(CreatePipelineRequestDTO request);
    List<PipelineResponseDTO> getAllPipeline();
    PipelineResponseDTO getPipelineById(Integer id);
    PipelineResponseDTO updatePipelineById(Integer id, UpdatePipelineRequestDTO request);
    void inactivatePipelineById(Integer id);
}
