package com.homeaid.repository;


import com.homeaid.domain.UserBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBoardRepository extends JpaRepository<UserBoard, Long> {

  Page<UserBoard> findByUserId(Long userId, Pageable pageable);

  Page<UserBoard> findByUserIdAndTitleContainingOrUserIdAndContentContaining(Long userId,
      String title, Long userId1,
      String content, Pageable pageable);

  Page<UserBoard> findByTitleContainingOrContentContaining(String title, String content,
      Pageable pageable);
}
