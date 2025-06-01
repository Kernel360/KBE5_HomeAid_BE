package com.homeaid.service;


import com.homeaid.domain.UserBoard;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.dto.response.PagedResponseDto;
import com.homeaid.dto.response.UserBoardListResponseDto;
import com.homeaid.exception.BoardErrorCode;
import com.homeaid.exception.CustomException;
import com.homeaid.repository.UserBoardRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserBoardServiceImpl implements UserBoardService {

  private final UserBoardRepository userBoardRepository;

  @Override
  @Transactional
  public UserBoard createBoard(UserBoard userBoard) {
    try {
      return userBoardRepository.save(userBoard);
    } catch (Exception e) {
      throw new CustomException(BoardErrorCode.BOARD_SAVE_FAILED);
    }
  }

  @Override
  @Transactional
  public UserBoard updateBoard(Long id, Long userId, UserRole role, UserBoard userBoard) {
    validateBoardId(id);

    UserBoard board = userBoardRepository.findById(id)
        .orElseThrow(() -> new CustomException(BoardErrorCode.BOARD_NOT_FOUND));

    updateAccess(board, userId, role);
    try {
      board.updateBoard(userBoard.getTitle(), userBoard.getContent());
      return board; // @Transactional에 의해 자동으로 변경사항 저장됨
    } catch (IllegalStateException e) {
      // 엔티티에서 발생한 답변 완료 게시글 수정 시도 예외
      if (e.getMessage().contains("답변이 완료된")) {
        throw new CustomException(BoardErrorCode.BOARD_ANSWERED_UPDATE_FORBIDDEN);
      }
      throw new CustomException(BoardErrorCode.BOARD_UPDATE_FORBIDDEN);
    } catch (Exception e) {
      throw new CustomException(BoardErrorCode.BOARD_UPDATE_FAILED);
    }
  }

  @Override
  @Transactional
  public void deleteBoard(Long id, Long userId, UserRole role) {

    validateBoardId(id);

    UserBoard board = userBoardRepository.findById(id)
        .orElseThrow(() -> new CustomException(BoardErrorCode.BOARD_NOT_FOUND));

    deleteAccess(board, userId, role);

    try {
      userBoardRepository.deleteById(id);
    } catch (Exception e) {
      throw new CustomException(BoardErrorCode.BOARD_DELETE_FAILED);
    }

  }

  @Override
  @Transactional(readOnly = true)
  public UserBoard getBoard(Long id, Long userId, UserRole role) {

    validateBoardId(id);

    UserBoard board = userBoardRepository.findById(id)
        .orElseThrow(() -> new CustomException(BoardErrorCode.BOARD_NOT_FOUND));

    viewAccess(board, userId, role);

    return board;
  }

  @Override
  @Transactional(readOnly = true)
  public PagedResponseDto<UserBoardListResponseDto> searchBoard(String keyword, Pageable pageable, Long userId,
      UserRole role) {
    try {
      Page<UserBoard> boardPage;

      if (keyword == null || keyword.trim().isEmpty()) {
        boardPage = userBoardRepository.findByUserId(userId, pageable);
      } else {
        boardPage = userBoardRepository.findByUserIdAndKeyword(userId,
            keyword, pageable);
      }

      List<UserBoardListResponseDto> content = boardPage.getContent()
          .stream()
          .map(UserBoardListResponseDto::toDto)
          .collect(Collectors.toList());

      return PagedResponseDto.of(
          content,
          boardPage.getNumber(),
          boardPage.getTotalPages(),
          (int) boardPage.getTotalElements(),
          boardPage.getSize()
      );
    } catch (CustomException e) {
      throw e;
    } catch (Exception e) {
      throw new CustomException(BoardErrorCode.BOARD_SEARCH_FAILED);
    }
  }

  private void validateBoardId(Long id) {
    if (id == null || id <= 0) {
      throw new CustomException(BoardErrorCode.INVALID_BOARD_ID);
    }
  }

  private boolean validateUserAccess(UserBoard board, Long userId, UserRole role) {
    if (!board.getUserId().equals(userId) || role != board.getRole()) {
      throw new CustomException(BoardErrorCode.BOARD_ACCESS_UNAUTHORIZED);
    }
    return true;
  }

  private void updateAccess(UserBoard board, Long userId, UserRole role) {
    if (!validateUserAccess(board, userId, role)) {
      throw new CustomException(BoardErrorCode.BOARD_UPDATE_FORBIDDEN);
    }
  }

  private void deleteAccess(UserBoard board, Long userId, UserRole role) {
    if (!validateUserAccess(board, userId, role)) {
      throw new CustomException(BoardErrorCode.BOARD_DELETE_FORBIDDEN);
    }
  }

  private void viewAccess(UserBoard board, Long userId, UserRole role) {
    if (!validateUserAccess(board, userId, role)) {
      throw new CustomException(BoardErrorCode.BOARD_VIEW_FORBIDDEN);
    }
  }

}
