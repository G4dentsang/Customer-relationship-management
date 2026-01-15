package com.b2b.b2b.modules.workflow.payloads;

import com.b2b.b2b.modules.workflow.enums.WorkflowConditionOperator;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkflowConditionDTO {
    Integer id;
    @NotBlank
    private String field;
    @NotBlank
    private WorkflowConditionOperator workflowConditionOperator;
    @NotBlank
    private String expectedValue;

}
