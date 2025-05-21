package com.example.homeaid.global.util;

import org.springframework.data.domain.Page;

public class PageUtil {

    /**
     * @param page
     * Page의 메서드 map()을 사용하여 Page<Dto>로 변환 합니다
     *      map의 인자 : fromEntity(Entity entity)
     * Page<Dto>로 변환한 객체를 파라미터로 받습니다
     * @return Dto(page.getContent) 와 paging 요소로 구성
     */
    public static <T> ResponsePagingDto<T> from(Page<T> page) {

        return new ResponsePagingDto<>(
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
