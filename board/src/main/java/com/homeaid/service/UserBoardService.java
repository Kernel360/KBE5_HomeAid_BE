package com.homeaid.service;


import com.homeaid.common.response.PagedResponseDto;
import com.homeaid.domain.UserBoard;
import com.homeaid.dto.response.UserBoardListResponseDto;
import org.springframework.data.domain.Pageable;

public interface UserBoardService {

  UserBoard createBoard(UserBoard userBoard);

  UserBoard updateBoard(Long id, Long userId, UserBoard userBoard);

  UserBoard getBoard(Long id, Long userId);

  PagedResponseDto<UserBoardListResponseDto> searchBoard(String keyword, Pageable pageable,
      Long userId);

  void deleteBoard(Long id, Long userId);

}
