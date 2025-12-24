package com.b2b.b2b.modules.crm.pipelineStage.service.impl;

import com.b2b.b2b.exception.APIException;
import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipeline.repository.PipelineRepository;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageRequestDTO;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageResponseDTO;
import com.b2b.b2b.modules.crm.pipelineStage.repository.PipelineStageRepository;
import com.b2b.b2b.modules.crm.pipelineStage.service.PipelineStageService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PipelineStageServiceImpl implements PipelineStageService
{
    private final PipelineStageRepository pipelineStageRepository;
    private final PipelineRepository pipelineRepository;

    public PipelineStageServiceImpl(PipelineStageRepository pipelineStageRepository, PipelineRepository pipelineRepository) {
        this.pipelineStageRepository = pipelineStageRepository;
        this.pipelineRepository = pipelineRepository;
    }

    @Override
    public PipelineStage findNextPipelineStage(Pipeline pipeline, Integer currentOrder) {
    return pipelineStageRepository.findNextStages(pipeline,currentOrder)
            .stream()
            .findFirst()
            .orElse(null);
    }

    @Override
    public List<PipelineStageResponseDTO> addPipelineStage(Integer pipelineId, List<PipelineStageRequestDTO> pipelineStageRequestDTOs, User user) {
       Organization organization = user.getUserOrganizations()
               .stream()
               .filter(userOrg -> userOrg.isPrimary())
               .findFirst()
               .orElseThrow(() -> new APIException("User's organization not found"))
               .getOrganization();

       Pipeline pipeline = pipelineRepository.findByIdAndOrganization(pipelineId, organization);
       if(pipeline == null){
           throw new ResourceNotFoundException("Pipeline", "id", pipelineId);
       }

       List<PipelineStage> existingStages = pipelineStageRepository.findAllByPipelineOrderByStageOrderAsc(pipeline);
       List<PipelineStage> addedStages = new ArrayList<>();

       for(PipelineStageRequestDTO stageDto: pipelineStageRequestDTOs){

           int requestedOrder = stageDto.getStageOrder();
           int maxStageOrder = existingStages.isEmpty() ? -1 : existingStages.getLast().getStageOrder();
           if(requestedOrder < 0 || requestedOrder > maxStageOrder + 1){
               throw new APIException("StageOrder out of range");
           }
           //shifting existing order
           for(PipelineStage stage: existingStages){
               if(stage.getStageOrder() >= requestedOrder){
                   stage.setStageOrder(stage.getStageOrder() + 1);
               }

           }
           pipelineStageRepository.saveAll(existingStages);
           //inserting new stages
           PipelineStage stage = new PipelineStage();
           stage.setStageName(stageDto.getStageName());
           stage.setStageDescription(stageDto.getStageDescription());
           stage.setStageOrder(requestedOrder);
           stage.setPipeline(pipeline);
           PipelineStage savedStage = pipelineStageRepository.save(stage);

           existingStages.add(savedStage);
           //getLast always stays highest order  value
           existingStages.sort(Comparator.comparingInt(PipelineStage::getStageOrder));

           addedStages.add(stage);
       }


        return addedStages.stream().map(stage ->{
            return new PipelineStageResponseDTO(
                      stage.getId(),
                      stage.getStageName(),
                      stage.getStageDescription(),
                      stage.getStageOrder()
              );
                }).toList();

    }

    @Override
    public  List<PipelineStageResponseDTO> getPipelineStage(Integer pipelineId, User user) {
        Organization organization = user.getUserOrganizations()
                .stream()
                .filter(userOrg -> userOrg.isPrimary())
                .findFirst()
                .orElseThrow(() -> new APIException("User's organization not found"))
                .getOrganization();
        Pipeline pipeline = pipelineRepository.findByIdAndOrganization(pipelineId, organization);
        List<PipelineStage> pipelineStages = pipelineStageRepository.findAllByPipelineOrderByStageOrderAsc(pipeline);
        return pipelineStages.stream().map(stage ->{
            return new PipelineStageResponseDTO(
                    stage.getId(),
                    stage.getStageName(),
                    stage.getStageDescription(),
                    stage.getStageOrder()
            );
        }).toList();
    }
}
