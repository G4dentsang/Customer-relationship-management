package com.b2b.b2b.modules.crm.pipeline.service;

import com.b2b.b2b.modules.crm.pipeline.entity.PipelineType;
import com.b2b.b2b.modules.crm.pipeline.payloads.CreatePipelineRequestDTO;
import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineFilterDTO;
import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineResponseDTO;
import com.b2b.b2b.modules.crm.pipeline.payloads.UpdatePipelineRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface PipelineService {

    <T extends PipelineAssignable> void assignDefaultPipeline(T entity, PipelineType type);
    PipelineResponseDTO createPipeline(CreatePipelineRequestDTO request);
    Page<PipelineResponseDTO> getAllPipeline(PipelineFilterDTO filter, Pageable pageable);
    PipelineResponseDTO getPipelineById(Integer id);
    PipelineResponseDTO updatePipelineById(Integer id, UpdatePipelineRequestDTO request);
    void inactivatePipelineById(Integer id);
}
