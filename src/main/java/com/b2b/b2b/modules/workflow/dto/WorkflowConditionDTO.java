package com.b2b.b2b.modules.workflow.dto;

import com.b2b.b2b.modules.workflow.enums.WorkflowConditionOperator;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkflowConditionDTO {
    @NotBlank
    private String field;
    @NotBlank
    private WorkflowConditionOperator workflowConditionOperator;
    @NotBlank
    private String expectedValue;

}
