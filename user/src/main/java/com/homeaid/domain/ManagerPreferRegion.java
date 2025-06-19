package com.homeaid.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor
public class ManagerPreferRegion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "availability_id", nullable = false)
  private ManagerAvailability availability;

  @Column(nullable = false)
  private String sido;

  @Column(nullable = false)
  private String sigungu;

  @Builder
  public ManagerPreferRegion(String sido, String sigungu) {
    this.sido = sido;
    this.sigungu = sigungu;
  }

}
