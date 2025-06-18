package com.homeaid.serviceoption.domain;

import com.homeaid.serviceoption.converter.FeatureListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "service_option")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ServiceOption {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 100, nullable = false)
  private String name;

  @Convert(converter = FeatureListConverter.class)
  @Column(columnDefinition = "TEXT")
  private List<String> features = new ArrayList<>();

  @Column(nullable = false)
  private Integer price;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  @Builder
  public ServiceOption(String name, List<String> features, Integer price) {
    this.name = name;
    this.features = features;
    this.price = price;
  }


  public void update(String name, Integer price) {
    if (name != null && !name.isBlank()) {
      this.name = name;
    }
    if (price != null) {
      this.price = price;
    }
  }
}