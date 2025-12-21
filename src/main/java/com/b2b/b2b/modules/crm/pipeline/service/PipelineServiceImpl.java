package com.b2b.b2b.modules.crm.pipeline.service;

import com.b2b.b2b.exception.APIException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipeline.entity.PipelineType;
import com.b2b.b2b.modules.crm.pipeline.payloads.CreatePipelineRequestDTO;
import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineResponseDTO;
import com.b2b.b2b.modules.crm.pipeline.repository.PipelineRepository;
import com.b2b.b2b.modules.crm.pipeline.util.PipelineUtil;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageRequestDTO;
import com.b2b.b2b.modules.crm.pipelineStage.repository.PipelineStageRepository;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;

@Service
public class PipelineServiceImpl implements PipelineService {

    private final PipelineRepository pipelineRepository;
    private final PipelineStageRepository pipelineStageRepository;
    private final PipelineUtil pipelineUtil;

    public PipelineServiceImpl(PipelineRepository pipelineRepository, PipelineStageRepository pipelineStageRepository, PipelineUtil pipelineUtil) {
        this.pipelineRepository = pipelineRepository;
        this.pipelineStageRepository = pipelineStageRepository;
        this.pipelineUtil = pipelineUtil;
    }

    @Override
    public PipelineResponseDTO createPipeline(CreatePipelineRequestDTO createPipelineRequestDTO, User user) {
        Organization organization = user.getUserOrganizations()
                .stream()
                .filter(userOrganization -> userOrganization.isPrimary())
                .findFirst()
                .orElseThrow(()-> new APIException("User's organization not found"))
                .getOrganization();

        Pipeline pipeline = new Pipeline();
        pipeline.setPipelineName(createPipelineRequestDTO.getPipelineName());
        pipeline.setPipelineType(createPipelineRequestDTO.getPipelineType());
        pipeline.setDefault(createPipelineRequestDTO.isDefault());
        pipeline.setOrganization(organization);
        Pipeline savedPipeline = pipelineRepository.save(pipeline);

        if(createPipelineRequestDTO.getStages() != null) {

            for(int i = 0; i < createPipelineRequestDTO.getStages().size(); i++) {
                PipelineStageRequestDTO  pipelineStageRequestDTO = createPipelineRequestDTO.getStages().get(i);
                PipelineStage pipelineStage = new PipelineStage();
                pipelineStage.setStageName(pipelineStageRequestDTO.getStageName());
                pipelineStage.setStageDescription(pipelineStageRequestDTO.getStageDescription());
                int stageOder = (pipelineStageRequestDTO.getStageOrder() != null) ? pipelineStageRequestDTO.getStageOrder() : i;
                pipelineStage.setStageOrder(stageOder);
                pipelineStage.setPipeline(savedPipeline);
                pipelineStageRepository.save(pipelineStage);

            }
        }else{
            PipelineStage pipelineStage = new PipelineStage();
            pipelineStage.setStageName("Default Stage");
            pipelineStage.setStageOrder(0);
            pipelineStage.setPipeline(savedPipeline);
            pipelineStageRepository.save(pipelineStage);

        }

        return pipelineUtil.createPipelineResponseDTO(savedPipeline);

    }

    @Override
    public List<PipelineResponseDTO> getAllPipeline( User user) {
        Organization organization = user.getUserOrganizations()
                .stream()
                .filter(userOrganization -> userOrganization.isPrimary())
                .findFirst()
                .orElseThrow(()-> new APIException("User's organization not found"))
                .getOrganization();
        List<Pipeline> pipelineList = pipelineRepository.findAllByOrganization(organization);
        return pipelineList.stream().map(pipeline -> pipelineUtil.createPipelineResponseDTO(pipeline)).toList();
    }

    @Override
    public PipelineResponseDTO getPipelineById(Integer pipelineId, User user) {
        Organization organization = user.getUserOrganizations()
                .stream()
                .filter(userOrganization -> userOrganization.isPrimary())
                .findFirst()
                .orElseThrow(()-> new APIException("User's organization not found"))
                .getOrganization();
        Pipeline pipelineFromDB = pipelineRepository.findByIdAndOrganization(pipelineId, organization);
        return pipelineUtil.createPipelineResponseDTO(pipelineFromDB);
    }

    @Override
    public <T extends PipelineAssignable> void assignDefaultPipeline(T entity, PipelineType pipelineType) {

        Pipeline pipeline =  pipelineRepository.findDefaultPipelineByOrganizationIdAndType(entity.getOrganization().getOrganizationId(), pipelineType);

        PipelineStage  pipelineStage = pipeline.getPipelineStages()
                .stream()
                .sorted(Comparator.comparingInt(PipelineStage -> PipelineStage.getStageOrder()))
                .findFirst()
                .orElseThrow(()-> new RuntimeException("PipelineStage not found"));

        entity.setPipeline(pipeline);
        entity.setPipelineStage(pipelineStage);
    }

}
