package com.b2b.b2b.modules.crm.deal.payloads;

import com.b2b.b2b.modules.crm.deal.entity.DealStatus;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DealUpdateDTO {
    private String dealName;
    private Double dealAmount;
    private DealStatus dealStatus;
    private Integer ownerId;

}
