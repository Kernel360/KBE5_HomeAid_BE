package com.homeaid.common.response;

import java.util.List;
import java.util.function.Function;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

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


  public static <T> PagedResponseDto<T> of(
      List<T> content,
      int currentPage,
      int totalPages,
      int totalElements,
      int size
  ) {
    return PagedResponseDto.<T>builder()
        .content(content)
        .currentPage(currentPage)
        .totalPages(totalPages)
        .totalElements(totalElements)
        .size(size)
        .first(currentPage == 0)
        .last(currentPage == totalPages - 1)
        .hasPrevious(currentPage > 0)
        .hasNext(currentPage < totalPages - 1)
        .build();
  }

  public static <T, R> PagedResponseDto<R> fromPage(Page<T> page, Function<T, R> mapper) {
    List<R> content = page.getContent().stream()
        .map(mapper)
        .toList();

    return PagedResponseDto.<R>builder()
        .content(content)
        .currentPage(page.getNumber())
        .totalPages(page.getTotalPages())
        .totalElements((int) page.getTotalElements())
        .size(page.getSize())
        .first(page.isFirst())
        .last(page.isLast())
        .hasPrevious(page.hasPrevious())
        .hasNext(page.hasNext())
        .build();
  }

}
