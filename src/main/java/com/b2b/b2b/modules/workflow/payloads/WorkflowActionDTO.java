package com.b2b.b2b.modules.workflow.payloads;

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

