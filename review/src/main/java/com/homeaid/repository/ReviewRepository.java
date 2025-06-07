package com.homeaid.repository;


import com.homeaid.domain.Review;
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

}
