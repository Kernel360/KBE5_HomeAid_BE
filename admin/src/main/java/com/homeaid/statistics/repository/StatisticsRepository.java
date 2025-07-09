package com.homeaid.statistics.repository;

import com.homeaid.statistics.domain.StatisticsEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticsRepository extends JpaRepository<StatisticsEntity, Long> {

  Optional<StatisticsEntity> findByYearAndMonthAndDay(int year, Integer month, Integer day);

}
// 날짜별로 저장된 통계 데이터를 DB에서 fallback 조회할 때도 활용