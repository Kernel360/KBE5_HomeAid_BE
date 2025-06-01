package com.homeaid.service;


import com.homeaid.domain.UserBoard;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.dto.response.PagedResponseDto;
import com.homeaid.dto.response.UserBoardListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserBoardService {

  UserBoard createBoard(UserBoard userBoard);

  UserBoard updateBoard(Long id, Long userId, UserRole role, UserBoard userBoard);

  UserBoard getBoard(Long id, Long userId, UserRole role);

  PagedResponseDto<UserBoardListResponseDto> searchBoard(String keyword, Pageable pageable, Long userId, UserRole role);

  void deleteBoard(Long id, Long userId, UserRole role);

}
