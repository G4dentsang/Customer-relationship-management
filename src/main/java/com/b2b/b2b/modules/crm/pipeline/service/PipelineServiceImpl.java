package com.b2b.b2b.modules.crm.pipeline.service;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.pipeline.payloads.CreatePipelineRequestDTO;
import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class PipelineServiceImpl implements PipelineService {
    @Override
    public PipelineResponseDTO createPipeline(CreatePipelineRequestDTO createPipelineRequestDTO, User user) {
        return null;
    }
}
