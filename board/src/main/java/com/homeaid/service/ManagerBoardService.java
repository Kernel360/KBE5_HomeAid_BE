package com.homeaid.service;


import com.homeaid.domain.ManagerBoard;
import com.homeaid.dto.request.UpdateManagerBoardRequestDto;

public interface ManagerBoardService {

  ManagerBoard createManagerBoard(ManagerBoard managerBoard);

  ManagerBoard getManagerBoard(Long id);

  ManagerBoard updateManagerBoard(Long id, UpdateManagerBoardRequestDto updateManagerBoardRequestDto);

  void deleteManagerBoard(Long id);

}
