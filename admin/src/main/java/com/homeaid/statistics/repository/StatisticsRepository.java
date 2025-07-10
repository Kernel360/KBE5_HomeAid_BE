package com.homeaid.statistics.repository;

import com.homeaid.statistics.domain.StatisticsEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticsRepository extends JpaRepository<StatisticsEntity, Long> {

  //Optional<StatisticsEntity> findByYearAndMonthAndDay(int year, Integer month, Integer day);
  @Query("""
  SELECT s FROM StatisticsEntity s
  WHERE s.year = :year
    AND (:month IS NULL OR s.month = :month)
    AND (:day IS NULL OR s.day = :day)
  """)
  Optional<StatisticsEntity> findByDate(@Param("year") int year,
      @Param("month") Integer month,
      @Param("day") Integer day);

}
// 날짜별로 저장된 통계 데이터를 DB에서 fallback 조회할 때도 활용