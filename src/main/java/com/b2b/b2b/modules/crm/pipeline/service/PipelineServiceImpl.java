package com.b2b.b2b.modules.crm.pipeline.service;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.lead.repository.LeadRepository;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipeline.payloads.CreatePipelineRequestDTO;
import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineResponseDTO;
import com.b2b.b2b.modules.crm.pipeline.repository.PipelineRepository;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import org.springframework.stereotype.Service;

@Service
public class PipelineServiceImpl implements PipelineService {
    private final PipelineRepository pipelineRepository;
    private final LeadRepository leadRepository;
    public PipelineServiceImpl(PipelineRepository pipelineRepository, LeadRepository leadRepository) {
        this.pipelineRepository = pipelineRepository;
        this.leadRepository = leadRepository;
    }

    @Override
    public PipelineResponseDTO createPipeline(CreatePipelineRequestDTO createPipelineRequestDTO, User user) {
        return null;
    }

    @Override
    public void assignDefaultPipeline(Lead lead) {
        Pipeline pipeline =  pipelineRepository.findDefaultPipelineByOrganizationId(lead.getOrganization().getOrganizationId());
        PipelineStage  pipelineStage = pipeline.getPipelineStages().getFirst();
        lead.setPipeline(pipeline);
        lead.setPipelineStage(pipelineStage);
        leadRepository.save(lead); //updated lead
    }

}
