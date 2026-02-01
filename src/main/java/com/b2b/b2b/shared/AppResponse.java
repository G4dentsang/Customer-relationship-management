package com.b2b.b2b.shared;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppResponse<T>{
    public boolean success;
    public String message;
    public T data;

}
