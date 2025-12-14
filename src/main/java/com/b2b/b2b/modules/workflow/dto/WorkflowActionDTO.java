package com.b2b.b2b.modules.workflow.dto;

import com.b2b.b2b.modules.workflow.enums.WorkflowActionType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkflowActionDTO {
    private WorkflowActionType workflowActionType;
    private String actionConfigJson;
}

