package com.homeaid.statistics.service;

import com.homeaid.exception.CustomException;
import com.homeaid.statistics.config.StatisticsConstants;
import com.homeaid.statistics.domain.StatisticsEntity;
import com.homeaid.statistics.dto.AdminStatisticsDto;
import com.homeaid.statistics.exception.StatisticsErrorCode;
import com.homeaid.statistics.repository.StatisticsRepository;
import com.homeaid.util.RedisKeyFactory;
import com.homeaid.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminStatisticsStorageServiceImpl implements AdminStatisticsStorageService {

  private final RedisUtil redisUtil;
  private final StatisticsRepository statisticsRepository;

  @Override
  public void save(AdminStatisticsDto dto) {
    String key = RedisKeyFactory.buildAdminStatisticsKey(dto.getYear(), dto.getMonth(), dto.getDay());
    redisUtil.save(key, dto, StatisticsConstants.STATISTICS_CACHE_TTL);

    StatisticsEntity entity = StatisticsEntity.fromDto(dto);
    statisticsRepository.save(entity);
  }

  @Override
  public AdminStatisticsDto loadOrThrow(int year, Integer month, Integer day) {
    String key = RedisKeyFactory.buildAdminStatisticsKey(year, month, day);

    // 1. Redis 조회
    Object cached = redisUtil.getObject(key);
    if (cached instanceof AdminStatisticsDto cachedDto) {
      log.info("[캐시 HIT] Redis에서 통계 데이터 조회 - key: {}", key);
      return cachedDto;
    }

    if (cached != null) {
      log.warn("[캐시 MISS] 타입 불일치 - key: {}", key);
    } else {
      log.warn("[캐시 MISS] Redis에 없음 - key: {}", key);
    }

    // 2. DB fallback
    return statisticsRepository.findByYearAndMonthAndDay(year, month, day)
        .map(entity -> {
          try {
            AdminStatisticsDto dto = entity.toDto();
            log.info("[DB Fallback] DB에서 통계 데이터를 조회하여 복원 - key: {}", key);
            redisUtil.save(key, dto, StatisticsConstants.STATISTICS_CACHE_TTL);
            return dto;
          } catch (Exception e) {
            log.error("[역직렬화 실패] - key: {}", key, e);
            throw new CustomException(StatisticsErrorCode.STATISTICS_DESERIALIZATION_FAILED);
          }
        })
        .orElseThrow(() -> {
          log.error("[DB MISS] 통계 데이터 없음 - key: {}", key);
          return new CustomException(StatisticsErrorCode.STATISTICS_NOT_FOUND);
        });
  }

}
