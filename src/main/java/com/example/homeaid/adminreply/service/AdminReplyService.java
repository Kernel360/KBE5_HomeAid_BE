package com.example.homeaid.adminreply.service;

import com.example.homeaid.adminreply.dto.Request.AdminReplyRequestDto;
import com.example.homeaid.adminreply.entity.AdminReply;
import com.example.homeaid.adminreply.entity.PostType;

public interface AdminReplyService {

  AdminReply createReply(PostType postType, Long postId, AdminReply adminReply);

  AdminReply updateReply(PostType postType, Long postId, String content);

  void deleteReply(Long replyId);
}
