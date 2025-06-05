package com.homeaid.domain;

import static com.homeaid.domain.enumerate.ReservationStatus.REQUESTED;

import com.homeaid.domain.enumerate.ReservationStatus;
import com.homeaid.serviceoption.domain.ServiceSubOption;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Reservation {

  @Id
  @GeneratedValue
  private Long id;

  private LocalDate requestedDate;

  private LocalTime requestedTime;

  private Integer totalPrice;

  private Integer totalDuration;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReservationStatus status = REQUESTED;

  private Long customerId;

  private Long managerId;

  private Long finalMatchingId;

  private Double latitude;

  private Double longitude;

  @Column(columnDefinition = "TEXT")
  private String customerMemo;

  @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
  private ReservationItem item;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdDate;

  @LastModifiedDate
  private LocalDateTime modifiedDate;

  private LocalDateTime deletedDate;

  @Builder
  public Reservation(Long customerId, LocalDate requestedDate, LocalTime requestedTime) {
    this.customerId = customerId;
    this.requestedDate = requestedDate;
    this.requestedTime = requestedTime;
  }

  public void addItem(ServiceSubOption serviceSubOption) {
    this.item = new ReservationItem(serviceSubOption);
    item.setReservation(this);
  }

  public void updateReservation(Reservation newReservation, int newTotalPrice, int newTotalDuration) {
    this.requestedDate = newReservation.getRequestedDate();
    this.requestedTime = newReservation.getRequestedTime();
    this.totalPrice = newTotalPrice;
    this.totalDuration = newTotalDuration;
  }

  public void softDelete() {
    this.deletedDate = LocalDateTime.now();
  }


  public void updateStatusCompleted() {
    this.status = ReservationStatus.COMPLETED;
  }
  
  public void confirmMatching(Long matchingId) {
    finalMatchingId = matchingId;

  }
}

