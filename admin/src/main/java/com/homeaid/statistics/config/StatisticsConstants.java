package com.homeaid.statistics.config;

import java.time.Duration;

public class StatisticsConstants {

  // 통계 데이터를 Redis에 저장할 때 TTL (30일)
  public static final Duration STATISTICS_CACHE_TTL = Duration.ofDays(30);

  // 스케줄러 락을 위한 TTL (중복 방지용, 10분)
  public static final Duration STATISTICS_LOCK_TTL = Duration.ofMinutes(10);

  private StatisticsConstants() {
    // 인스턴스화 방지
  }

}
/**
 * 응집도(Cohesion) : StatisticsConstants는 통계 모듈에서만 쓰이는 TTL, 락 관련 상수를 담고 있으므로, 해당 도메인(statistics) 아래에 있어야 함
 * 재사용 가능성 : 이 상수들은 다른 도메인(e.g. 결제, 유저, 정산 등)에서 사용할 일이 거의 없으므로 글로벌 위치는 부적합
 * 관심사 분리(SRP) : 글로벌 config는 전역 설정 (JWT 설정, Spring 설정 등)을 위한 것이어야 하는데 도메인 설정을 혼합하면 모듈 구조가 흐트러짐
 * 확장성 고려 : 나중에 통계 모듈에만 적용되는 상수가 늘어날 경우, 전용 config 패키지 아래 관리하면 훨씬 유연하게 대응
 */