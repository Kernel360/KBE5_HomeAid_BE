package com.homeaid.boardreply.service;

import com.homeaid.boardreply.domain.BoardReply;

public interface BoardReplyService {

  BoardReply createReply(BoardReply boardReply);

  BoardReply updateReply(Long boardId, Long replyId, Long adminId, BoardReply boardReply);

  void deleteReply(Long boardId, Long replyId, Long adminId);
}
