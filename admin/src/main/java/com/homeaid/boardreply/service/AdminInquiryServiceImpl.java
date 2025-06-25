package com.homeaid.boardreply.service;

import com.homeaid.boardreply.dto.response.BoardReplyListResponseDto;
import com.homeaid.boardreply.dto.response.InquiryDetailResponseDto;
import com.homeaid.boardreply.exception.BoardReplyErrorCode;
import com.homeaid.boardreply.repository.AdminBoardReplyRepository;
import com.homeaid.domain.BoardReply;
import com.homeaid.domain.UserBoard;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.dto.response.UserBoardListResponseDto;
import com.homeaid.exception.BoardErrorCode;
import com.homeaid.exception.CustomException;
import com.homeaid.repository.UserBoardRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminInquiryServiceImpl implements AdminInquiryService {

  private final AdminBoardReplyRepository adminBoardReplyRepository;
  private final UserBoardRepository userBoardRepository;

  @Override
  public Page<UserBoardListResponseDto> getAllUserBoards(Pageable pageable) {
    return userBoardRepository.findAllWithUserName(pageable);
  }

  // 전체 답변 목록 조회
  @Override
  public Page<BoardReplyListResponseDto> getAllReplies(UserRole role, String keyword, Pageable pageable) {
    Page<BoardReply> replies;

    if (role != null) {
      replies = adminBoardReplyRepository.findByUserRole(role, pageable);
    } else if (keyword != null && !keyword.isBlank()) {
      replies = adminBoardReplyRepository.searchByUserName(keyword, pageable);
    } else {
      replies = adminBoardReplyRepository.findAll(pageable);
    }

    return replies.map(reply ->
        BoardReplyListResponseDto.toDto(reply, reply.getUser() != null ? reply.getUser().getName() : null)
    );
  }

  // 답변 단건 조회
  @Override
  public InquiryDetailResponseDto getReplyById(Long replyId) {
    BoardReply reply = adminBoardReplyRepository.findById(replyId)
        .orElseThrow(() -> new CustomException(BoardReplyErrorCode.REPLY_NOT_FOUND));

    String userName = reply.getUser() != null ? reply.getUser().getName() : null;

    return InquiryDetailResponseDto.from(reply, userName);
  }

  // 특정 유저의 답변 목록 조회
  @Override
  public Page<BoardReplyListResponseDto> getRepliesByUserId(Long userId, Pageable pageable) {
    Page<BoardReply> replies = adminBoardReplyRepository.findByUserIdWithUser(userId, pageable);

    return replies.map(reply ->
        BoardReplyListResponseDto.toDto(reply, reply.getUser() != null ? reply.getUser().getName() : null)
    );
  }

  // 답변 등록
  @Override
  @Transactional
  public BoardReply createReply(BoardReply boardReply) {
    // 게시글 ID에 대한 답변이 이미 존재하면 예외
    if (adminBoardReplyRepository.existsByBoardId(boardReply.getBoardId())) {
      throw new CustomException(BoardReplyErrorCode.REPLY_ALREADY_EXISTS);
    }

    // 답변 저장
    BoardReply savedReply = adminBoardReplyRepository.save(boardReply);

    // 게시글 찾아서 상태 변경
    UserBoard userBoard = userBoardRepository.findById(boardReply.getBoardId())
        .orElseThrow(() -> new CustomException(BoardErrorCode.BOARD_NOT_FOUND));

    userBoard.setReplyId(savedReply.getId());  // reply_id 연결
    userBoard.setAnswered();                   // 답변 완료 상태로 변경

    userBoardRepository.save(userBoard); // 변경사항 반영

    return savedReply;
  }

  // 답변 수정
  @Override
  @Transactional
  public BoardReply updateReply(Long boardId, Long replyId, Long adminId, BoardReply requestReply) {
    validateReplyId(replyId);

    BoardReply reply = adminBoardReplyRepository.findById(replyId)
        .orElseThrow(() -> new CustomException(BoardReplyErrorCode.REPLY_NOT_FOUND));

    validateAdminAccess(adminId, reply);
    reply.updateReply(requestReply.getContent());

    return reply;
  }

  // 답변 삭제
  @Override
  @Transactional
  public void deleteReply(Long boardId, Long replyId, Long adminId) {
    validateReplyId(replyId);

    BoardReply reply = adminBoardReplyRepository.findById(replyId)
        .orElseThrow(() -> new CustomException(BoardReplyErrorCode.REPLY_NOT_FOUND));

    validateAdminAccess(adminId, reply);
    adminBoardReplyRepository.delete(reply);
  }

  // 유효성 검사
  private void validateReplyId(Long replyId) {
    if (replyId == null || replyId <= 0) {
      throw new CustomException(BoardReplyErrorCode.INVALID_REPLY_ID);
    }
  }

  private void validateAdminAccess(Long adminId, BoardReply reply) {
    if (!reply.getAdminId().equals(adminId)) {
      throw new CustomException(BoardReplyErrorCode.REPLY_ACCESS_UNAUTHORIZED);
    }
  }
}