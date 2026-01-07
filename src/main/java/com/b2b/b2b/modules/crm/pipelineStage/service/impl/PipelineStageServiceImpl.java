package com.b2b.b2b.modules.crm.pipelineStage.service.impl;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.deal.entity.DealStatus;
import com.b2b.b2b.modules.crm.deal.repository.DealRepository;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.lead.repository.LeadRepository;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipeline.entity.PipelineType;
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
    private final LeadRepository leadRepository;
    private final DealRepository dealRepository;

    @Override
    @Transactional
    public List<PipelineStageResponseDTO> addPipelineStage(Integer id, List<PipelineStageRequestDTO> request, User user) {
        Pipeline pipeline = getPipeline(id, user);
        List<PipelineStage> existingStages = pipelineStageRepository.findAllByPipelineOrderByStageOrderAsc(pipeline);
        int totalStageAfterAddedStages = existingStages.size() + request.size();

        for (PipelineStageRequestDTO stageDto : request) {
            int newStageOrder = stageDto.getStageOrder();
            if (newStageOrder < 1) newStageOrder = 1;
            if (newStageOrder > totalStageAfterAddedStages) newStageOrder = totalStageAfterAddedStages;

            PipelineStage newStage = convertToEntity(stageDto, pipeline);
            newStage.setStageOrder(newStageOrder);
            existingStages.add(newStage);
        }

        //SORT AND RE-INDEX PIPELINE STAGES
        sortAndReindex(existingStages);

        pipelineStageRepository.saveAll(existingStages);
        return toDTOList(existingStages);
    }

    @Override
    @Transactional
    public List<PipelineStageResponseDTO> updatePipelineStageById(Integer pipelineId, Integer stageId, PipelineStageRequestDTO request, User user) {
        Pipeline pipeline = getPipeline(pipelineId, user);
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
        reIndex(allStages);

        return toDTOList(pipelineStageRepository.saveAll(allStages));
    }

    @Override
    public List<PipelineStageResponseDTO> getAllPipelineStage(Integer id, User user) {
        Pipeline pipeline = getPipeline(id, user);
        return toDTOList(pipelineStageRepository.findAllByPipelineOrderByStageOrderAsc(pipeline));
    }

    @Override
    @Transactional
    public List<PipelineStageResponseDTO> deletePipelineStageById(Integer pipelineId, Integer stageId, Integer targetStageId, User user) {
        Organization org = getOrg(user);
        Pipeline pipeline = getPipeline(pipelineId, user);
        List<PipelineStage> allStages = pipelineStageRepository.findAllByPipelineOrderByStageOrderAsc(pipeline);
        PipelineStage stageToDelete = allStages.stream()
                .filter(s -> s.getId().equals(stageId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("PipelineStage", "id", stageId));

        long totalCount = (pipeline.getPipelineType() == PipelineType.LEAD)
                ? leadRepository.countByPipelineStage(stageToDelete)
                : dealRepository.countByPipelineStage(stageToDelete);

        if (totalCount > 0) {
            validateMigrationTarget(stageId, targetStageId, totalCount, allStages);

            //migration
            if (pipeline.getPipelineType() == PipelineType.LEAD) {
                leadRepository.bulkMigrateBetweenStage(stageId, targetStageId, org.getOrganizationId());
            } else {
                dealRepository.bulkMigrateBetweenStage(stageId, targetStageId, org.getOrganizationId());
            }
        }
        allStages.remove(stageToDelete);
        pipelineStageRepository.delete(stageToDelete);

        reIndex(allStages);

        pipelineStageRepository.saveAll(allStages);
        return toDTOList(allStages);
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

    /***********Helper methods*************/


    private Organization getOrg(User user) {
        return authUtil.getPrimaryOrganization(user);
    }

    private List<PipelineStageResponseDTO> toDTOList(List<PipelineStage> stages) {
        return stages.stream().map(pipelineStageUtils::createPipelineStageResponseDTO).toList();
    }

    private PipelineStage convertToEntity(PipelineStageRequestDTO request, Pipeline pipeline) {
        PipelineStage stage = modelMapper.map(request, PipelineStage.class);
        stage.setPipeline(pipeline);
        return stage;
    }

    private static void sortAndReindex(List<PipelineStage> existingStages) {
        //ordering/sorting stages and then inserting based on weight
        existingStages.sort(Comparator.comparingInt(PipelineStage::getStageOrder)
                .thenComparing(s -> s.getId() == null ? 1 : 0));

        //syncing the stage order in sequence 1,2,3.......
        reIndex(existingStages);
    }

    private static void reIndex(List<PipelineStage> existingStages) {
        for (int i = 0; i < existingStages.size(); i++) {
            existingStages.get(i).setStageOrder(i + 1);
        }
    }

    private static void validateMigrationTarget(Integer stageId, Integer targetStageId, long totalCount, List<PipelineStage> allStages) {
        if (targetStageId == null)
            throw new IllegalArgumentException(String.format("Stage contains %d records. You must provide a migration target stage.", totalCount));
        if (stageId.equals(targetStageId))
            throw new IllegalArgumentException("Target stage cannot be the same as the deleted stage.");

        boolean targetExists = allStages.stream().anyMatch(s -> s.getId().equals(targetStageId));

        if (!targetExists) throw new IllegalArgumentException("The target stage does not exist in this pipeline.");
    }

    private Pipeline getPipeline(Integer pipelineId, User user) {
        return pipelineRepository.findByIdAndOrganization(pipelineId, getOrg(user))
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline", "id", pipelineId));
    }

}
