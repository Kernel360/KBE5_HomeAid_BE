package com.homeaid.domain;

import com.homeaid.domain.enumerate.MatchingStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Matching {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reservation_id", nullable = false)
  private Reservation reservation;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "manager_id")
  private Manager manager;


  @Column(nullable = false)
  private int matchingRound;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MatchingStatus status; // ex. CONFIRMED, REJECTED


  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MatchingStatus managerStatus; // REQUESTED, ACCEPTED, REJECTED


  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MatchingStatus customerStatus; // WAITING, CONFIRMED, REJECTED

  private LocalDateTime matchedAt;

  @Column(columnDefinition = "TEXT")
  private String managerMemo;

  @Column(columnDefinition = "TEXT")
  private String customerMemo;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdDate;

  @LastModifiedDate
  private LocalDateTime modifiedDate;


  public void setReservationAndManagerAndMatchingRound(Reservation reservation, Manager manager, int matchingRound) {
    this.reservation = reservation;
    this.manager = manager;
    this.matchingRound = matchingRound;
  }

  @Builder
  public Matching(MatchingStatus status, MatchingStatus managerStatus, MatchingStatus customerStatus) {
    this.status = status;
    this.managerStatus = managerStatus;
    this.customerStatus = customerStatus;
  }

  public void acceptByManager() {
    this.managerStatus = MatchingStatus.ACCEPTED;
    this.status = MatchingStatus.ACCEPTED;
//    this.reservation.setFinalMatchingId(this.id);
  }

  public void rejectByManager(String memo) {
    this.managerStatus = MatchingStatus.REJECTED;
    this.status = MatchingStatus.REJECTED;
    this.managerMemo = memo;
  }

  public void confirmByCustomer() {
    this.customerStatus = MatchingStatus.CONFIRMED;
    this.status = MatchingStatus.CONFIRMED;
    this.matchedAt = LocalDateTime.now();
    this.reservation.setFinalMatchingId(this);
  }

  public void rejectByCustomer(String memo) {
    this.customerStatus = MatchingStatus.REJECTED;
    this.status = MatchingStatus.REJECTED;
    this.customerMemo = memo;
    this.reservation.setFinalMatchingId(null);
  }

  public boolean isCompleted() {
    return this.status == MatchingStatus.CONFIRMED;
  }
}