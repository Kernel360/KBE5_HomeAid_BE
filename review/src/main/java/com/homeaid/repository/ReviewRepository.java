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

  Page<Review> findByTargetId(Long targetId, Pageable pageable);
  
  Page<Review> findAllByWriterRole(UserRole writerRole, Pageable pageable);

  // 관리자 권한별 조회
  @Query("""
      SELECT r FROM Review r
      WHERE (:writerRole IS NULL OR r.writerRole = :writerRole)
      ORDER BY r.createdAt DESC
  """)
  Page<Review> findByWriterRoleCondition(@Param("writerRole") UserRole writerRole, Pageable pageable);

  // 매니저 전체 평균 평점
  @Query("""
      SELECT COALESCE(AVG(r.rating), 0)
      FROM Review r
      WHERE r.writerRole = 'CUSTOMER'
        AND YEAR(r.createdAt) = :year
        AND (:month IS NULL OR MONTH(r.createdAt) = :month)
        AND (:day IS NULL OR DAY(r.createdAt) = :day)
  """)
  Double findAvgRatingForManagers(@Param("year") int year, @Param("month") Integer month, @Param("day") Integer day
  );

  // 매니저 대상 총 리뷰 수
  @Query("""
      SELECT COUNT(r)
      FROM Review r
      WHERE r.writerRole = 'CUSTOMER'
        AND YEAR(r.createdAt) = :year
        AND (:month IS NULL OR MONTH(r.createdAt) = :month)
        AND (:day IS NULL OR DAY(r.createdAt) = :day)
  """)
  Long countReviewsForManagers(@Param("year") int year, @Param("month") Integer month, @Param("day") Integer day
  );

}
