package com.homeaid.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PagedResponseDto<T> {

  private List<T> content;
  private int currentPage;
  private int totalPages;
  private int totalElements;
  private int size;
  private boolean first;
  private boolean last;
  private boolean hasPrevious;
  private boolean hasNext;

  public static <T> PagedResponseDto<T> of(List<T> content, int currentPage, int totalPages, int totalElements, int size) {
    return PagedResponseDto.<T>builder()
        .content(content)
        .currentPage(currentPage)
        .totalPages(totalPages)
        .totalElements(totalElements)
        .size(size)
        .first(currentPage == 0)
        .last(currentPage == totalPages - 1)
        .hasPrevious(currentPage < totalPages - 1)
        .hasNext(currentPage > 0)
        .build();
  }

}
