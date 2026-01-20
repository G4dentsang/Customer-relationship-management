package com.b2b.b2b.modules.crm.pipelineStage.service;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.deal.entity.DealStatus;
import com.b2b.b2b.modules.crm.deal.repository.DealRepository;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.lead.repository.LeadRepository;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipeline.entity.PipelineType;
import com.b2b.b2b.modules.crm.pipeline.service.PipelineAssignable;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageRequestDTO;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageResponseDTO;
import com.b2b.b2b.modules.crm.pipelineStage.repository.PipelineStageRepository;
import com.b2b.b2b.modules.workflow.events.DomainEventPublisher;
import com.b2b.b2b.modules.workflow.events.PipelineStageChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PipelineStageServiceImpl implements PipelineStageService
{
    private final PipelineStageRepository pipelineStageRepository;
    private final DomainEventPublisher domainEventPublisher;
    private final LeadRepository leadRepository;
    private final DealRepository dealRepository;
    private final Helpers helpers;

    @Override
    @Transactional
    public List<PipelineStageResponseDTO> addPipelineStage(Integer id, List<PipelineStageRequestDTO> request) {
        Pipeline pipeline = helpers.getPipeline(id);
        List<PipelineStage> existingStages = pipelineStageRepository.findAllByPipelineOrderByStageOrderAsc(pipeline);
        int totalStageAfterAddedStages = existingStages.size() + request.size();

        for (PipelineStageRequestDTO stageDto : request) {
            int newStageOrder = stageDto.getStageOrder();
            if (newStageOrder < 1) newStageOrder = 1;
            if (newStageOrder > totalStageAfterAddedStages) newStageOrder = totalStageAfterAddedStages;

            PipelineStage newStage = helpers.convertToEntity(stageDto, pipeline);
            newStage.setStageOrder(newStageOrder);
            existingStages.add(newStage);
        }

        //SORT AND RE-INDEX PIPELINE STAGES
        helpers.sortAndReindex(existingStages);

        pipelineStageRepository.saveAll(existingStages);
        return helpers.toDTOList(existingStages);
    }

    @Override
    @Transactional
    public List<PipelineStageResponseDTO> updatePipelineStageById(Integer pipelineId, Integer stageId, PipelineStageRequestDTO request) {
        Pipeline pipeline = helpers.getPipeline(pipelineId);
        List<PipelineStage> allStages = pipelineStageRepository.findAllByPipelineOrderByStageOrderAsc(pipeline);
        PipelineStage stageToUpdate = allStages.stream()
                .filter(s -> s.getId().equals(stageId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("PipelineStage", "id", stageId));

        int newStageOrder = request.getStageOrder();
        if (newStageOrder < 1) newStageOrder = 1;
        if (newStageOrder > allStages.size()) newStageOrder = allStages.size();

        stageToUpdate.setStageName(request.getStageName());
        stageToUpdate.setStageOrder(newStageOrder);

        allStages.sort(Comparator.comparingInt(PipelineStage::getStageOrder)
                .thenComparing(s -> s.getId().equals(stageId) ? 0 : 1));
        helpers.reIndex(allStages);

        return helpers.toDTOList(pipelineStageRepository.saveAll(allStages));
    }

    @Override
    public List<PipelineStageResponseDTO> getAllPipelineStage(Integer id) {
        Pipeline pipeline = helpers.getPipeline(id);
        return helpers.toDTOList(pipelineStageRepository.findAllByPipelineOrderByStageOrderAsc(pipeline));
    }

    @Override
    @Transactional
    public List<PipelineStageResponseDTO> deletePipelineStageById(Integer pipelineId, Integer stageId, Integer targetStageId) {
        Pipeline pipeline = helpers.getPipeline(pipelineId);
        List<PipelineStage> allStages = pipelineStageRepository.findAllByPipelineOrderByStageOrderAsc(pipeline);
        PipelineStage stageToDelete = allStages.stream()
                .filter(s -> s.getId().equals(stageId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("PipelineStage", "id", stageId));

        long totalCount = (pipeline.getPipelineType() == PipelineType.LEAD)
                ? leadRepository.countByPipelineStage(stageToDelete)
                : dealRepository.countByPipelineStage(stageToDelete);

        if (totalCount > 0) {
            helpers.validateMigrationTarget(stageId, targetStageId, totalCount, allStages);

            //migration
            if (pipeline.getPipelineType() == PipelineType.LEAD) {
                leadRepository.bulkMigrateBetweenStage(stageId, targetStageId);
            } else {
                dealRepository.bulkMigrateBetweenStage(stageId, targetStageId);
            }
        }
        allStages.remove(stageToDelete);
        pipelineStageRepository.delete(stageToDelete);

        helpers.reIndex(allStages);

        pipelineStageRepository.saveAll(allStages);
        return helpers.toDTOList(allStages);
    }

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

}
