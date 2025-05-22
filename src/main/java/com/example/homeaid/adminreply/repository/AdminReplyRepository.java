package com.example.homeaid.adminreply.repository;

import com.example.homeaid.adminreply.entity.AdminReply;
import com.example.homeaid.adminreply.entity.PostType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminReplyRepository extends JpaRepository<AdminReply, Long> {

  Optional<AdminReply> findByPostTypeAndPostId(PostType postType, Long postId);

}
