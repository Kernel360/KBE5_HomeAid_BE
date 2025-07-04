package com.homeaid.repository;

import com.homeaid.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByPhone(String phone);

  Optional<User> findByPhone(String phone);

  @Query("SELECT COUNT(u) FROM User u")
  long countAllUsers();

  // 전체 가입자 수 (연/월/일)
  @Query("""
      SELECT COUNT(u) FROM User u
      WHERE YEAR(u.createdAt) = :year
        AND (:month IS NULL OR MONTH(u.createdAt) = :month)
        AND (:day IS NULL OR DAY(u.createdAt) = :day)
      """)
  long countSignUps(@Param("year") int year, @Param("month") Integer month, @Param("day") Integer day
  );

  // 전체 탈퇴자 수 (연/월/일 , 논리삭제 기준)
  @Query("""
      SELECT COUNT(u) FROM User u
      WHERE u.deleted = true
        AND YEAR(u.createdAt) = :year
        AND (:month IS NULL OR MONTH(u.createdAt) = :month)
        AND (:day IS NULL OR DAY(u.createdAt) = :day)
      """)
  long countWithdrawn(@Param("year") int year, @Param("month") Integer month, @Param("day") Integer day
  );

}