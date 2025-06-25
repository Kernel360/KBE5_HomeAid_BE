package com.homeaid.repository;


import com.homeaid.domain.UserBoard;
import com.homeaid.dto.response.UserBoardListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBoardRepository extends JpaRepository<UserBoard, Long> {

  Page<UserBoard> findByUserId(Long userId, Pageable pageable);

  @Query("SELECT ub FROM UserBoard ub WHERE ub.userId = :userId AND " +
      "(ub.title LIKE %:keyword% OR ub.content LIKE %:keyword%)")
  Page<UserBoard> findByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);

  // [관리자] 문의글 목록 전체 조회
  @Query("""
    SELECT new com.homeaid.dto.response.UserBoardListResponseDto(
      ub.id, ub.userId, u.name, ub.title, ub.content, ub.createdAt
    )
    FROM UserBoard ub
    JOIN User u ON ub.userId = u.id
  """)
  Page<UserBoardListResponseDto> findAllWithUserName(Pageable pageable);

}
