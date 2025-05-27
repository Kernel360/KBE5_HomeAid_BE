package com.homeaid.serviceoption.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "service_option")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

  @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ServiceSubOption> subOptions = new ArrayList<>();

  public ServiceOption(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public void addSubOption(ServiceSubOption subOption) {
    subOptions.add(subOption);
    subOption.setOption(this);
  }
}