package com.example.homeaid.admin.adminreply.service;

import com.example.homeaid.admin.adminreply.entity.AdminReply;
import com.example.homeaid.admin.adminreply.entity.PostType;

public interface AdminReplyService {

  AdminReply createReply(PostType postType, Long postId, AdminReply adminReply);

  AdminReply updateReply(PostType postType, Long postId, String content);

  void deleteReply(Long replyId);
}
