package com.b2b.b2b.modules.crm.pipelineStage.service.impl;

import com.b2b.b2b.exception.APIException;
import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.deal.entity.DealStatus;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipeline.repository.PipelineRepository;
import com.b2b.b2b.modules.crm.pipeline.service.PipelineAssignable;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageRequestDTO;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageResponseDTO;
import com.b2b.b2b.modules.crm.pipelineStage.repository.PipelineStageRepository;
import com.b2b.b2b.modules.crm.pipelineStage.service.PipelineStageService;
import com.b2b.b2b.modules.crm.pipelineStage.util.PipelineStageUtils;
import com.b2b.b2b.modules.workflow.events.DomainEventPublisher;
import com.b2b.b2b.modules.workflow.events.PipelineStageChangeEvent;
import com.b2b.b2b.shared.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PipelineStageServiceImpl implements PipelineStageService
{
    private final PipelineStageRepository pipelineStageRepository;
    private final PipelineRepository pipelineRepository;
    private final AuthUtil authUtil;
    private final PipelineStageUtils pipelineStageUtils;
    private final ModelMapper modelMapper;
    private final DomainEventPublisher domainEventPublisher;


    @Override
    public Optional<PipelineStage> findNextPipelineStage(Pipeline pipeline, Integer currentOrder) {
    return pipelineStageRepository.findNextStages(pipeline,currentOrder);
    }

    @Override
    @Transactional
    public void promoteToNextStage(PipelineAssignable entity) {
        PipelineStage oldStage = entity.getPipelineStage();
        findNextPipelineStage(entity.getPipeline(), oldStage.getStageOrder())
                .ifPresentOrElse(
                        nextStage -> {
                            entity.setPipelineStage(nextStage);
                            domainEventPublisher.publishEvent(new PipelineStageChangeEvent(entity, oldStage, nextStage));
                            log.info("{} of id: {} moved from {} to {}",
                                    entity.getClass().getSimpleName(), entity.getId(),
                                    oldStage.getStageName(), nextStage.getStageName());
                        },
                        () -> handleEndOfPipeline(entity)
                );
    }

    private void handleEndOfPipeline(PipelineAssignable entity) {
        if (entity instanceof Lead lead) {
            lead.setReadyForConversion(true);
            log.info("Lead {} is now ready for conversion.", lead.getId());
        }
        else if (entity instanceof Deal deal) {
            if(deal.getDealStatus() != DealStatus.CLOSED_WON){
                deal.setDealStatus(DealStatus.CLOSED_WON);
                deal.setClosedAt(LocalDateTime.now());
            }
            log.info("Deal {} reached the end of pipeline and is marked WON.", deal.getId());
        }
    }

    @Override
    @Transactional
    public List<PipelineStageResponseDTO> addPipelineStage(Integer id, List<PipelineStageRequestDTO> request, User user) {

       Pipeline pipeline = pipelineRepository.findByIdAndOrganization(id, getOrg(user))
               .orElseThrow(() -> new ResourceNotFoundException("Pipeline", "id", id));

       List<PipelineStage> existingStages = pipelineStageRepository.findAllByPipelineOrderByStageOrderAsc(pipeline);
       List<PipelineStage> newStagesList = new ArrayList<>();

       for(PipelineStageRequestDTO stageDto: request){
           int requestedOrder = stageDto.getStageOrder();
           validateOrderRange(requestedOrder, existingStages);

           shiftExistingStages(existingStages, requestedOrder);

           PipelineStage newStage = convertToEntity(stageDto, pipeline);

           existingStages.add(newStage);
           newStagesList.add(newStage);

           existingStages.sort(Comparator.comparingInt(PipelineStage::getStageOrder));
       }
        pipelineStageRepository.saveAll(existingStages );
        return toDTOList(newStagesList);
    }

    @Override
    public  List<PipelineStageResponseDTO> getPipelineStage(Integer id, User user) {
        Pipeline pipeline = pipelineRepository.findByIdAndOrganization(id, getOrg(user))
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline", "id", id));;
        return toDTOList(pipelineStageRepository.findAllByPipelineOrderByStageOrderAsc(pipeline));
    }

/***********Helper methods*************/

    private Organization getOrg(User user) {
        return authUtil.getPrimaryOrganization(user);
    }

    private List<PipelineStageResponseDTO> toDTOList(List<PipelineStage> stages) {
        return stages.stream().map(pipelineStageUtils::createPipelineStageResponseDTO).toList();
    }

    private void validateOrderRange(int requestedOrder, List<PipelineStage> stages) {
        int maxStageOrder = stages.isEmpty() ? 0 : stages.getLast().getStageOrder() + 1;
        if (requestedOrder < 0 || requestedOrder > maxStageOrder + 1) {
            throw new APIException("Stage order " + requestedOrder + " is out of range.");
        }
    }

    private void shiftExistingStages(List<PipelineStage> stages, int requestedOrder) {
        stages.stream().filter(s -> s.getStageOrder() >= requestedOrder)
                .forEach(s -> s.setStageOrder(s.getStageOrder() + 1));

    }

    private PipelineStage convertToEntity(PipelineStageRequestDTO request, Pipeline pipeline) {
        PipelineStage stage = modelMapper.map(request, PipelineStage.class);
        stage.setStageOrder(request.getStageOrder());
        stage.setPipeline(pipeline);
        return stage;
    }
}
