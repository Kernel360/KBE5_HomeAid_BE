package com.homeaid.serviceoption.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

  @Column(columnDefinition = "TEXT")
  private String description;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ServiceSubOption> subOptions = new ArrayList<>();

  @Builder
  public ServiceOption(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public void addSubOption(ServiceSubOption subOption) {
    subOptions.add(subOption);
    subOption.setOption(this);
  }

  public void update(String name, String description) {
    if (name != null && !name.isBlank()) {
      this.name = name;
    }
    if (description != null) {
      this.description = description;
    }
  }
}