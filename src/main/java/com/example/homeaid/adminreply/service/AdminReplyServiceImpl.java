package com.example.homeaid.adminreply.service;

import com.example.homeaid.adminreply.dto.Request.AdminReplyRequestDto;
import com.example.homeaid.adminreply.entity.AdminReply;
import com.example.homeaid.adminreply.entity.PostType;
import com.example.homeaid.adminreply.repository.AdminReplyRepository;
import com.example.homeaid.customerboard.repository.CustomerBoardRepository;
import com.example.homeaid.global.exception.CustomException;
import com.example.homeaid.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminReplyServiceImpl implements AdminReplyService {

  private final AdminReplyRepository adminReplyRepository;
//  private final ManagerBoardRepository managerBoardRepository;
  private final CustomerBoardRepository customerBoardRepository;

  @Override
  @Transactional
  public AdminReply createReply(PostType postType, Long postId, AdminReply adminReply) {

    // 1. 이미 답변이 존재하는지 확인
    if (adminReplyRepository.findByPostTypeAndPostId(postType, postId).isPresent()) {
      throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
    }

    // 2. 해당 게시글 존재 여부 확인
    switch (postType) {
//      case MANAGER -> {
//        if (!managerQnaRepository.existsById(postId)) {
//          throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
//        }
//      }
      case CUSTOMER -> {
        if (!customerBoardRepository.existsById(postId)) {
          throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
        }
      }
      default -> throw new CustomException(ErrorCode.INVALID_REQUEST);
    }

    adminReply.createReply(postType,postId, 1L); // adminId 수정 필요
    return adminReplyRepository.save(adminReply);
  }

  @Override
  @Transactional
  public AdminReply updateReply(PostType postType, Long postId, String content) {
    AdminReply reply = adminReplyRepository
        .findByPostTypeAndPostId(postType, postId)
        .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

    reply.updateReply(content);
    return reply;
  }


  @Override
  public void deleteReply(Long replyId) {
    adminReplyRepository.findById(replyId).orElseThrow(() -> new CustomException(
        ErrorCode.RESOURCE_NOT_FOUND
    ));

    adminReplyRepository.deleteById(replyId);
  }

}



