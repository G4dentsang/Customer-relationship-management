package com.b2b.b2b.modules.crm.pipeline.service;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.pipeline.payloads.CreatePipelineRequestDTO;
import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineResponseDTO;


public interface PipelineService {
    PipelineResponseDTO createPipeline(CreatePipelineRequestDTO createPipelineRequestDTO, User user);
    void assignDefaultPipeline(Lead lead);
}
