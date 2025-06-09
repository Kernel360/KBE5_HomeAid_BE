package com.homeaid.boardreply.service;

import com.homeaid.boardreply.domain.BoardReply;
import com.homeaid.boardreply.exception.BoardReplyErrorCode;
import com.homeaid.boardreply.repository.BoardReplyRepository;
import com.homeaid.exception.BoardErrorCode;
import com.homeaid.exception.CustomException;
import com.homeaid.repository.UserBoardRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardReplyServiceImpl implements BoardReplyService {

  private final BoardReplyRepository boardReplyRepository;
  private final UserBoardRepository userBoardRepository;

  @Override
  @Transactional
  public BoardReply createReply(BoardReply boardReply) {
    validateBoardId(boardReply.getBoardId());
    return boardReplyRepository.save(boardReply);
  }

  @Override
  public BoardReply updateReply(Long boardId, Long replyId, Long adminId, BoardReply boardReply) {
    validateBoardId(boardId);
    validateReplyId(replyId);
    validateUserAccess(adminId, boardReply);
    return boardReply.updateReply(boardReply.getContent());
  }

  @Override
  public void deleteReply(Long boardId, Long replyId, Long adminId) {
    validateBoardId(boardId);
    validateReplyId(replyId);
    BoardReply boardReply = boardReplyRepository.findById(replyId).orElse(null);
    validateUserAccess(adminId, Objects.requireNonNull(boardReply));
    userBoardRepository.deleteById(replyId);
  }

  private void validateBoardId(Long boardId) {
    if (boardId == null || boardId <= 0) {
      throw new CustomException(BoardErrorCode.INVALID_BOARD_ID);
    }

    if(!userBoardRepository.existsById(boardId)) {
      throw new CustomException(BoardErrorCode.BOARD_NOT_FOUND);
    }
  }

  private void validateReplyId(Long replyId) {
    if (replyId == null || replyId <= 0) {
      throw new CustomException(BoardReplyErrorCode.INVALID_REPLY_ID);
    }

    if(!boardReplyRepository.existsById(replyId)) {
      throw new CustomException(BoardReplyErrorCode.REPLY_NOT_FOUND);
    }
  }

  private boolean validateUserAccess(Long adminId, BoardReply boardReply) {
    if (!boardReply.getAdminId().equals(adminId)) {
      throw new CustomException(BoardReplyErrorCode.REPLY_ACCESS_UNAUTHORIZED);
    }
    return true;
  }
}


