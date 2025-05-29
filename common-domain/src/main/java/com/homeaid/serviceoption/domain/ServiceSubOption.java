package com.homeaid.serviceoption.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "service_sub_option")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ServiceSubOption {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 100, nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private Integer durationMinutes;

  @Column(nullable = false)
  private Integer basePrice;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "option_id")
  private ServiceOption option;

  @Builder
  public ServiceSubOption(String name, String description, Integer durationMinutes, Integer basePrice, ServiceOption option) {
    this.name = name;
    this.description = description;
    this.durationMinutes = durationMinutes;
    this.basePrice = basePrice;
    this.option = option;
  }

  public void update(String name, String description, Integer durationMinutes, Integer basePrice) {
    if (name != null && !name.isBlank()) {
      this.name = name;
    }
    if (description != null) {
      this.description = description;
    }
    if (durationMinutes != null && durationMinutes > 0) {
      this.durationMinutes = durationMinutes;
    }
    if (basePrice != null && basePrice > 0) {
      this.basePrice = basePrice;
    }
  }

}