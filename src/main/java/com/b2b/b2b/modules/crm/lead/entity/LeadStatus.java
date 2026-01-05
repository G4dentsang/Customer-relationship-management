package com.b2b.b2b.modules.crm.lead.entity;

import lombok.Getter;

@Getter
public enum LeadStatus {
    //ACTIVE group
    NEW(1),
    CONTACTED(1),
    INTERESTED(1),

    //PENDING group
    WAITING_FOR_REPLY(2),
    ON_HOLD(2),

    //TERMINAL group
    DISQUALIFIED(3),
    CONVERTED(3),
    SOFT_DELETED(3);

    private final int groupId;
    LeadStatus(int groupId)
    {
        this.groupId = groupId;
    }
}
