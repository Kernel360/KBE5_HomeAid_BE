package com.example.homeaid.manager.managerboard.service;

import com.example.homeaid.admin.adminreply.dto.Request.AdminReplyDto;
import com.example.homeaid.admin.adminreply.entity.AdminReply;
import com.example.homeaid.admin.adminreply.entity.PostType;
import com.example.homeaid.admin.adminreply.repository.AdminReplyRepository;
import com.example.homeaid.global.exception.CustomException;
import com.example.homeaid.global.exception.ErrorCode;
import com.example.homeaid.manager.managerboard.dto.request.ManagerBoardWithRepliesDto;
import com.example.homeaid.manager.managerboard.dto.request.UpdateManagerBoardRequestDto;
import com.example.homeaid.manager.managerboard.entity.ManagerBoard;
import com.example.homeaid.manager.managerboard.repository.ManagerBoardRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManagerBoardServiceImpl implements ManagerBoardService {

  private final ManagerBoardRepository managerBoardRepository;
  private final AdminReplyRepository adminReplyRepository;

  public List<ManagerBoard> getAll() {
    return managerBoardRepository.findAll(); // 기본 JPA 메소드 사용 가능
  }

  @Override
  public ManagerBoard createManagerBoard(ManagerBoard managerBoard) {
    return managerBoardRepository.save(managerBoard);
  }

  @Override
  public ManagerBoard getManagerBoard(Long id) {
    ManagerBoard managerBoard = managerBoardRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
    return managerBoard;
  }

  @Override
  public ManagerBoard updateManagerBoard(Long id,
      UpdateManagerBoardRequestDto updateManagerBoardRequestDto) {
    ManagerBoard existing = managerBoardRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

    ManagerBoard toSave = ManagerBoard.builder()
        .id(existing.getId())
        .title(updateManagerBoardRequestDto.getTitle())
        .content(updateManagerBoardRequestDto.getContent())
        .isPublic(updateManagerBoardRequestDto.getIsPublic())
        .createdAt(existing.getCreatedAt())
        .managerId(existing.getManagerId())
        .build();

    return managerBoardRepository.save(toSave);
  }

  @Override
  public void deleteManagerBoard(Long id) {
    managerBoardRepository.deleteById(id);
  }

  public ManagerBoardWithRepliesDto getBoardWithReplies(Long boardId) {
    ManagerBoard board = managerBoardRepository.findById(boardId)
        .orElseThrow(() -> new RuntimeException("게시글 없음"));

    Optional<AdminReply> optionalReply = adminReplyRepository.findByPostTypeAndPostId(
        PostType.MANAGER, boardId);

    AdminReplyDto replyDto = optionalReply
        .map(r -> AdminReplyDto.builder()
            .id(r.getId())
            .content(r.getContent())
            .createdAt(r.getCreatedAt())
            .build())
        .orElse(null);

    return ManagerBoardWithRepliesDto.builder()
        .id(board.getId())
        .title(board.getTitle())
        .content(board.getContent())
        .reply(replyDto)
        .build();
  }
}
