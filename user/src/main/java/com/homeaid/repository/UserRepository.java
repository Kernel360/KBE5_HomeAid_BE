package com.homeaid.repository;

import com.homeaid.domain.User;
import java.time.LocalDateTime;
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

  // 연도/월별 가입자 수 (월이 null이면 연도만 기준)
  @Query("""
      SELECT COUNT(u) FROM User u
      WHERE YEAR(u.createdAt) = :year
      AND (:month IS NULL OR MONTH(u.createdAt) = :month)
      """)
  long countSignUps(@Param("year") int year, @Param("month") Integer month);

  // 연도/월별 탈퇴 회원 수 (논리삭제 기준)
  @Query("""
      SELECT COUNT(u) FROM User u
      WHERE u.deleted = true AND YEAR(u.createdAt) = :year
      AND (:month IS NULL OR MONTH(u.createdAt) = :month)
      """)
  long countWithdrawn(@Param("year") int year, @Param("month") Integer month);

  // 연도/월별 탈퇴율 계산 (Native SQL 사용)
  @Query(value = """
      SELECT (COUNT(*) * 1.0 /
        NULLIF((SELECT COUNT(*) FROM user u2 WHERE YEAR(u2.created_at) = :year
        AND (:month IS NULL OR MONTH(u2.created_at) = :month)), 0)) * 100
      FROM user u
      WHERE u.deleted = true AND YEAR(u.created_at) = :year
      AND (:month IS NULL OR MONTH(u.created_at) = :month)
      """, nativeQuery = true)
  Double calculateWithdrawRate(@Param("year") int year, @Param("month") Integer month);

  // 장기 미접속자 수 조회 (예: 최근 6개월간 로그인 없음)
  @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt < :threshold")
  long countInactiveUsers(@Param("threshold") LocalDateTime threshold);
}