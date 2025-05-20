package com.example.homeaid.managerboard.service;

import com.example.homeaid.global.exception.CustomException;
import com.example.homeaid.global.exception.ErrorCode;
import com.example.homeaid.managerboard.dto.request.ManagerBoardUpdateRequestDto;
import com.example.homeaid.managerboard.entity.ManagerBoard;
import com.example.homeaid.managerboard.repository.ManagerBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManagerBoardServiceImpl implements ManagerBoardService {

  private final ManagerBoardRepository managerBoardRepository;

  @Override
  public ManagerBoard createManagerBoard(ManagerBoard managerBoard) {
    return managerBoardRepository.save(managerBoard);
  }

  @Override
  public ManagerBoard updateManagerBoard(Long id, ManagerBoardUpdateRequestDto dto) {
    ManagerBoard updateBoard = managerBoardRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

    ManagerBoard updated = ManagerBoard.builder()
        .id(updateBoard.getId())
        .title(dto.getTitle())
        .content(dto.getContent())
        .isPublic(dto.getIsPublic())
        .createdAt(updateBoard.getCreatedAt()) // 기존 생성일 유지
        .managerId(updateBoard.getManagerId()) // 작성자 변경 없음
        .build();
    return managerBoardRepository.save(updated);
  }

  @Override
  public ManagerBoard getManagerBoard(Long id) {
    ManagerBoard managerBoard = managerBoardRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
    return managerBoard;
  }

  @Override
  public void deleteManagerBoard(Long id) {
    managerBoardRepository.deleteById(id);
  }

}
