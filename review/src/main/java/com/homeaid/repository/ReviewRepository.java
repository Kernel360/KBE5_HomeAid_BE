package com.homeaid.repository;


import com.homeaid.domain.Review;
import com.homeaid.domain.enumerate.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

  @Query("SELECT COUNT(r) FROM Review r WHERE r.targetId = :targetId")
  Integer getReviewCountByTargetId(@Param("targetId") Long targetId);

  @Query("SELECT  AVG(r.rating) FROM Review r WHERE r.targetId = :targetId")
  Double getAverageReviewRatingByTargetId(@Param("targetId") Long targetId);

  Page<Review> findByWriterId(Long userId, Pageable pageable);

  Page<Review> findAllByWriterRole(UserRole writerRole, Pageable pageable);

  // 관리자 권한별 조회
  @Query("""
      SELECT r FROM Review r
      WHERE (:writerRole IS NULL OR r.writerRole = :writerRole)
      ORDER BY r.createdAt DESC
  """)
  Page<Review> findByWriterRoleCondition(@Param("writerRole") UserRole writerRole, Pageable pageable);

}
