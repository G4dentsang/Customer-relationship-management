package com.b2b.b2b.shared.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaginationMeta {
    private int currentPage;
    private long totalElements;
    private int  totalPages;
    private int pageSize;
    boolean lastPage;

}
