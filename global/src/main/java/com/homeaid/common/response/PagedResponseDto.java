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
  private long currentPage;
  private long totalPages;
  private long totalElements;
  private long size;
  private boolean first;
  private boolean last;
  private boolean hasPrevious;
  private boolean hasNext;


  public static <T> PagedResponseDto<T> of(
      List<T> content,
      long currentPage,
      long totalPages,
      long totalElements,
      long size
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
        .totalElements(page.getTotalElements())
        .size(page.getSize())
        .first(page.isFirst())
        .last(page.isLast())
        .hasPrevious(page.hasPrevious())
        .hasNext(page.hasNext())
        .build();
  }

  public static <R> PagedResponseDto<R> fromPage(Page<R> page) {
    return PagedResponseDto.<R>builder()
        .content(page.getContent())
        .currentPage(page.getNumber())
        .totalPages(page.getTotalPages())
        .totalElements(page.getTotalElements())
        .size(page.getSize())
        .first(page.isFirst())
        .last(page.isLast())
        .hasPrevious(page.hasPrevious())
        .hasNext(page.hasNext())
        .build();
  }

}
