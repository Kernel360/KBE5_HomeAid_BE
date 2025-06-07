package com.homeaid.domain;


import com.homeaid.serviceoption.domain.ServiceSubOption;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ReservationItem {

  @Id
  @GeneratedValue
  private Long id;

  private String subOptionName;

  private Integer basePrice;

  private Integer duration;

  @Setter
  @OneToOne(fetch = FetchType.LAZY)
  private Reservation reservation;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdDate;

  @LastModifiedDate
  private LocalDateTime modifiedDate;

  private LocalDateTime deletedDate;

  public ReservationItem(ServiceSubOption serviceSubOption) {
    this.subOptionName = serviceSubOption.getName();
    this.basePrice = serviceSubOption.getBasePrice();
    this.duration = serviceSubOption.getDurationMinutes();
  }

  public void updateItem(ServiceSubOption subOption) {
    this.subOptionName = subOption.getName();
    this.basePrice = subOption.getBasePrice();
    this.duration = subOption.getDurationMinutes();
  }

  public void softDelete() {
    this.deletedDate = LocalDateTime.now();
  }


}