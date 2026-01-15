package com.b2b.b2b.modules.workflow.payloads;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkflowRuleUpdateMetaDataDTO {
    private String name;
    private String description;
}
