package com.b2b.b2b.modules.crm.lead.model;

import lombok.Getter;

@Getter
public enum LeadStatus {

    NEW(StatusCategory.OPEN, "New"),
    CONTACTED(StatusCategory.OPEN, "Contacted"),
    QUALIFIED(StatusCategory.OPEN, "Qualified"),


    WAITING_FOR_REPLY(StatusCategory.PENDING, "Waiting for Reply"),
    ON_HOLD(StatusCategory.PENDING, "On Hold"),
    READY_FOR_CONVERSION(StatusCategory.PENDING, "Ready to Convert"),


    CONVERTED(StatusCategory.CLOSED, "Converted"),
    LOST(StatusCategory.CLOSED, "Lost"),
    SOFT_DELETED(StatusCategory.CLOSED, "Deleted");  //to think if needed

    private final StatusCategory category; ;
    private final String label;

    LeadStatus(StatusCategory category, String label) {
        this.category = category;
        this.label = label;
    }

    public boolean isOpen() {
        return this.category == StatusCategory.OPEN;
    }

    public boolean isClosed() {
        return this.category == StatusCategory.CLOSED;
    }

    public enum StatusCategory{
        OPEN,
        PENDING,
        CLOSED
    }
}
