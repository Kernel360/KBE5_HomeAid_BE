package com.homeaid.boardreply.repository;

import com.homeaid.domain.BoardReply;
import com.homeaid.domain.enumerate.UserRole;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminBoardReplyRepository extends JpaRepository<BoardReply, Long> {

  // 특정 게시글에 대한 답변 존재 여부
  boolean existsByBoardId(Long boardId);

  // 특정 게시글에 대한 관리자 답변 조회
  Optional<BoardReply> findByBoardId(Long boardId);

  @Query("SELECT r FROM BoardReply r JOIN FETCH r.user WHERE r.boardId = :boardId")
  Optional<BoardReply> findWithUserByBoardId(@Param("boardId") Long boardId);

  // 🔍 역할 기반 조회
  Page<BoardReply> findByUserRole(UserRole role, Pageable pageable);

  // 🔍 사용자 이름 키워드 기반 검색
  @Query("SELECT r FROM BoardReply r JOIN r.user u WHERE u.name LIKE %:keyword%")
  Page<BoardReply> searchByUserName(@Param("keyword") String keyword, Pageable pageable);

  // 🔍 특정 유저 ID로 답변 목록 조회
  @Query("SELECT r FROM BoardReply r JOIN FETCH r.user WHERE r.user.id = :userId")
  Page<BoardReply> findByUserIdWithUser(@Param("userId") Long userId, Pageable pageable);

}
