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

  // 연도별 가입자 수
  @Query("SELECT COUNT(u) FROM User u WHERE YEAR(u.createdAt) = :year")
  long countSignUpsByYear(@Param("year") int year);

  // 연도별 탈퇴 회원 수 (논리삭제 기준)
  @Query("SELECT COUNT(u) FROM User u WHERE u.deleted = true AND YEAR(u.createdAt) = :year")
  long countWithdrawnByYear(@Param("year") int year);

  // 장기 미접속자 (마지막 로그인일 기준 필요 시 lastLoginDate 필드 추가 필요)
  @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt < :threshold")
  long countInactiveUsers(@Param("threshold") LocalDateTime threshold);

  // 연도별 탈퇴율 계산 (논리삭제 기준)
  @Query(
      value = "SELECT (COUNT(*) * 1.0 / " +
          "NULLIF((SELECT COUNT(*) FROM user u2 WHERE YEAR(u2.created_at) = :year), 0)) * 100 " +
          "FROM user u WHERE u.deleted = true AND YEAR(u.created_at) = :year",
      nativeQuery = true
  )
  Double calculateWithdrawRate(@Param("year") int year);
}

