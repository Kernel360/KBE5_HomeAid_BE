package com.homeaid.service;


import com.homeaid.common.response.PagedResponseDto;
import com.homeaid.domain.UserBoard;
import com.homeaid.dto.response.UserBoardListResponseDto;
import com.homeaid.exception.BoardErrorCode;
import com.homeaid.exception.CustomException;
import com.homeaid.repository.UserBoardRepository;
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
  public UserBoard updateBoard(Long id, Long userId, UserBoard userBoard) {
    validateBoardId(id);

    UserBoard board = userBoardRepository.findById(id)
        .orElseThrow(() -> new CustomException(BoardErrorCode.BOARD_NOT_FOUND));

    updateAccess(board, userId);

    if (board.isAnswered()) {
      throw new CustomException(BoardErrorCode.BOARD_ANSWERED_UPDATE_FORBIDDEN);
    }

    try {
      board.updateBoard(userBoard.getTitle(), userBoard.getContent());
      return board; // @Transactional에 의해 자동으로 변경사항 저장됨
    } catch (IllegalStateException e) {
      throw new CustomException(BoardErrorCode.BOARD_UPDATE_FORBIDDEN);
    } catch (Exception e) {
      throw new CustomException(BoardErrorCode.BOARD_UPDATE_FAILED);
    }
  }

  @Override
  @Transactional
  public void deleteBoard(Long id, Long userId) {

    validateBoardId(id);

    UserBoard board = userBoardRepository.findById(id)
        .orElseThrow(() -> new CustomException(BoardErrorCode.BOARD_NOT_FOUND));

    deleteAccess(board, userId);

    try {
      userBoardRepository.deleteById(id);
    } catch (Exception e) {
      throw new CustomException(BoardErrorCode.BOARD_DELETE_FAILED);
    }

  }

  @Override
  @Transactional(readOnly = true)
  public UserBoard getBoard(Long id, Long userId) {

    validateBoardId(id);

    UserBoard board = userBoardRepository.findById(id)
        .orElseThrow(() -> new CustomException(BoardErrorCode.BOARD_NOT_FOUND));

    viewAccess(board, userId);

    return board;
  }

  @Override
  @Transactional(readOnly = true)
  public PagedResponseDto<UserBoardListResponseDto> searchBoard(String keyword, Pageable pageable,
      Long userId) {
    try {
      Page<UserBoard> boardPage;

      if (keyword == null || keyword.trim().isEmpty()) {
        boardPage = userBoardRepository.findByUserId(userId, pageable);
      } else {
        boardPage = userBoardRepository.findByUserIdAndKeyword(userId,
            keyword, pageable);
      }

      return PagedResponseDto.fromPage(boardPage, UserBoardListResponseDto::toDto);

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

  private boolean validateUserAccess(UserBoard board, Long userId) {
    if (!board.getUserId().equals(userId)) {
      throw new CustomException(BoardErrorCode.BOARD_ACCESS_UNAUTHORIZED);
    }
    return true;
  }

  private void updateAccess(UserBoard board, Long userId) {
    if (!validateUserAccess(board, userId)) {
      throw new CustomException(BoardErrorCode.BOARD_UPDATE_FORBIDDEN);
    }
  }

  private void deleteAccess(UserBoard board, Long userId) {
    if (!validateUserAccess(board, userId)) {
      throw new CustomException(BoardErrorCode.BOARD_DELETE_FORBIDDEN);
    }
  }

  private void viewAccess(UserBoard board, Long userId) {
    if (!validateUserAccess(board, userId)) {
      throw new CustomException(BoardErrorCode.BOARD_VIEW_FORBIDDEN);
    }
  }

}
