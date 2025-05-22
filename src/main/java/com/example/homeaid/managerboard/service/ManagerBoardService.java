package com.example.homeaid.managerboard.service;

import com.example.homeaid.managerboard.dto.request.UpdateManagerBoardRequestDto;
import com.example.homeaid.managerboard.entity.ManagerBoard;

public interface ManagerBoardService {

  ManagerBoard createManagerBoard(ManagerBoard managerBoard);

  ManagerBoard getManagerBoard(Long id);

  ManagerBoard updateManagerBoard(Long id, UpdateManagerBoardRequestDto updateManagerBoardRequestDto);

  void deleteManagerBoard(Long id);

}
