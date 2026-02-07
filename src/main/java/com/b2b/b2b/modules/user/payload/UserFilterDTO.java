package com.b2b.b2b.modules.user.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFilterDTO {
    private String searchText;
    private Boolean userActive;
    private Boolean emailVarified;
}
