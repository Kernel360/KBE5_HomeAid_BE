package com.homeaid.service;


import com.homeaid.domain.UserBoard;
import com.homeaid.domain.enumerate.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserBoardService {

  UserBoard createBoard(UserBoard userBoard);

  UserBoard updateBoard(Long id, Long userId, UserRole role, UserBoard userBoard);

  UserBoard getBoard(Long id, Long userId, UserRole role);

  Page searchBoard(String keyword, Pageable pageable, Long userId, UserRole role);

  void deleteBoard(Long id, Long userId, UserRole role);

}
