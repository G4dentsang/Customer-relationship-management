package com.b2b.b2b.modules.workflow.dto;

import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkflowRuleCreateDTO {
    @NotBlank
    private String name;
    private String description;
    private WorkflowTriggerType workflowTriggerType;
    private boolean isActive;
    private List<WorkflowConditionDTO> workflowConditions = new ArrayList<>();
    private List<WorkflowActionDTO> workflowActions = new ArrayList<>();

}
