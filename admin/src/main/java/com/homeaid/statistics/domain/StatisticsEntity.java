package com.homeaid.statistics.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeaid.exception.CustomException;
import com.homeaid.statistics.dto.AdminStatisticsDto;
import com.homeaid.statistics.exception.StatisticsErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "admin_statistics")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class StatisticsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private int year;
  private Integer month;
  private Integer day;

  @Lob
  private String jsonData; // AdminStatisticsDto 전체를 JSON 문자열로 저장

  @CreatedDate
  @Column(updatable = false, nullable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt; // 마지막 갱신 시점 자동 기록

  public static StatisticsEntity fromDto(AdminStatisticsDto dto) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      return StatisticsEntity.builder()
          .year(dto.getYear())
          .month(dto.getMonth())
          .day(dto.getDay())
          .jsonData(mapper.writeValueAsString(dto)) // DTO → JSON 문자열
          .build();
    } catch (JsonProcessingException e) {
      throw new CustomException(StatisticsErrorCode.STATISTICS_SERIALIZATION_FAILED);
    }
  }

  public AdminStatisticsDto toDto() {
    try {
      ObjectMapper mapper = new ObjectMapper()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      return mapper.readValue(this.jsonData, AdminStatisticsDto.class);
    } catch (JsonProcessingException e) {
      throw new CustomException(StatisticsErrorCode.STATISTICS_DESERIALIZATION_FAILED);
    }
  }

}
