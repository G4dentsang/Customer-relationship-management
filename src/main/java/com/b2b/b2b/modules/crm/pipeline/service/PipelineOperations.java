package com.b2b.b2b.modules.crm.pipeline.service;

import com.b2b.b2b.modules.organization.model.Organization;
import com.b2b.b2b.modules.crm.pipeline.model.BasePipeline;
import com.b2b.b2b.modules.crm.pipeline.payloads.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PipelineOperations <P extends BasePipeline> {
    PipelineResponseDTO createPipeline(CreatePipelineRequestDTO2 request);

    Page<PipelineResponseDTO> getAllPipeline(PipelineFilterDTO filter, Pageable pageable);

    PipelineResponseDTO getPipeline(Integer pipelineId);

    PipelineResponseDTO updatePipelineById(Integer id, UpdatePipelineRequestDTO request);

    void inactivatePipeline(Integer pipelineId);

    P createDefaultPipeline(Organization org);

    void migrateAndInactivate(Integer sourceId, PipelineMigrationRequestDTO request);

}
