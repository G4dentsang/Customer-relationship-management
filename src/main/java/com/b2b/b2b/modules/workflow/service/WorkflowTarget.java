package com.b2b.b2b.modules.workflow.service;

import com.b2b.b2b.modules.auth.entity.Organization;

public interface WorkflowTarget {
    Integer getId();
    Organization getOrganization();
}
