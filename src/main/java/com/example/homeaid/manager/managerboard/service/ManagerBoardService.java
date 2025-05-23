package com.example.homeaid.manager.managerboard.service;

import com.example.homeaid.manager.managerboard.dto.request.ManagerBoardWithRepliesDto;
import com.example.homeaid.manager.managerboard.dto.request.UpdateManagerBoardRequestDto;
import com.example.homeaid.manager.managerboard.entity.ManagerBoard;

public interface ManagerBoardService {

  ManagerBoard createManagerBoard(ManagerBoard managerBoard);

  ManagerBoard getManagerBoard(Long id);

  ManagerBoard updateManagerBoard(Long id, UpdateManagerBoardRequestDto updateManagerBoardRequestDto);

  void deleteManagerBoard(Long id);

  ManagerBoardWithRepliesDto getBoardWithReplies(Long boardId);

}
