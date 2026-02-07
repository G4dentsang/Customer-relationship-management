package com.b2b.b2b.shared.response;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PaginatedResponse <T>
{
    private List<T> items;
    private PaginationMeta pagination;

    public PaginatedResponse(Page<T> page) {
        this.items = page.getContent();
        this.pagination = PaginationMeta.builder()
                .currentPage(page.getNumber())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .lastPage(page.isLast())
                .build();

    }
}
