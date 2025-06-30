package com.homeaid.boardreply.service;

import com.homeaid.boardreply.dto.response.BoardReplyListResponseDto;
import com.homeaid.boardreply.dto.response.InquiryDetailResponseDto;
import com.homeaid.boardreply.dto.response.InquiryWithReplyResponseDto;
import com.homeaid.domain.BoardReply;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.dto.response.UserBoardListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminInquiryService {

  // 전체 문의글 목록 조회
  Page<UserBoardListResponseDto> getAllUserBoards(Pageable pageable);

  // 전체 답변 목록 조회 (역할/키워드 필터링)
  Page<BoardReplyListResponseDto> getAllReplies(UserRole role, String keyword, Pageable pageable);

  // 특정 답변 상세 조회
  InquiryDetailResponseDto getReplyById(Long replyId);

  // 특정 유저의 답변 조회
  Page<BoardReplyListResponseDto> getRepliesByUserId(Long userId, Pageable pageable);

  // 답변 등록
  BoardReply createReply(BoardReply boardReply);

  // 답변 수정
  BoardReply updateReply(Long boardId, Long replyId, Long adminId, BoardReply boardReply);

  // 답변 삭제
  void deleteReply(Long boardId, Long replyId, Long adminId);

  // 문의글 + 답변
  InquiryWithReplyResponseDto getBoardWithReply(Long boardId);
}
