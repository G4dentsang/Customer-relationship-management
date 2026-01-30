package com.b2b.b2b.shared;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@MappedSuperclass
@FilterDef(
        name = "organizationFilter",
        parameters = @ParamDef(name = "orgId", type = Integer.class)
)
@Filter(
        name = "organizationFilter",
        condition = "organization_id = :orgId"    //WHERE
)
@Getter
@Setter
public abstract class BaseEntity {
    @Column(name = "organization_id",  insertable = false, updatable = false)
    private Integer orgId;
}
