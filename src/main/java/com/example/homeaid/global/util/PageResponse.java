package com.example.homeaid.global.util;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;

}
