package com.example.homeaid.global.util;

import org.springframework.data.domain.Page;

public class PageUtil {

    /**
     * Page<Dto>객체를 파라미터로 받아
     * Dto객체와 Page의 paging하는 요소로 구성
     * @param page
     * @return
     * @param <T>
     */
    public static <T> PageResponse<T> from(Page<T> page) {

        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getTotalPages(),
            page.getTotalElements(),
            page.getSize(),
            page.hasNext(),
            page.hasPrevious()
        );
    }

}
