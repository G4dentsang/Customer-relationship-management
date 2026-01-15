package com.b2b.b2b.modules.workflow.payloads;

import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;
import lombok.*;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkflowRuleUpdateDTO {
    Integer id;
    WorkflowTriggerType workflowTriggerType;
    List<WorkflowConditionDTO> workflowConditions;
    List<WorkflowActionDTO> workflowActions;
}
