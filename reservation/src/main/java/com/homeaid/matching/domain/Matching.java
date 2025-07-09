package com.homeaid.matching.domain;

import com.homeaid.domain.Manager;
import com.homeaid.matching.controller.enumerate.MatchingStatus;
import com.homeaid.reservation.domain.Reservation;
import com.homeaid.worklog.domain.WorkLog;
import com.homeaid.worklog.domain.enumerate.WorkType;
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

  @Setter
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
  private MatchingStatus status;


  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MatchingStatus managerStatus;


  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MatchingStatus customerStatus;

  private LocalDateTime matchedAt;

  @Column(columnDefinition = "TEXT")
  private String managerMemo;

  @Column(columnDefinition = "TEXT")
  private String customerMemo;

  @OneToOne(mappedBy = "matching", cascade = CascadeType.ALL, orphanRemoval = true)
  private WorkLog workLog;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdDate;

  @LastModifiedDate
  private LocalDateTime modifiedDate;

  public static Matching create(Manager manger, int matchingRound) {
    return Matching.builder()
        .status(MatchingStatus.REQUESTED)
        .managerStatus(MatchingStatus.REQUESTED)
        .customerStatus(MatchingStatus.REQUESTED)
        .manager(manger)
        .matchingRound(matchingRound)
        .build();
  }

  @Builder
  public Matching(MatchingStatus status, MatchingStatus managerStatus,
      MatchingStatus customerStatus, Manager manager, int matchingRound) {
    this.status = status;
    this.managerStatus = managerStatus;
    this.customerStatus = customerStatus;
    this.manager = manager;
    this.matchingRound = matchingRound;
  }

  public void acceptByManager() {
    this.managerStatus = MatchingStatus.ACCEPTED;
    this.status = MatchingStatus.ACCEPTED;
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
  }

  public void rejectByCustomer(String memo) {
    this.customerStatus = MatchingStatus.REJECTED;
    this.status = MatchingStatus.REJECTED;
    this.customerMemo = memo;
    this.reservation.setFinalMatching(null);
  }

  public void finalizeMatching() {
    this.reservation.setFinalMatching(this);
    this.createWorkLog();
  }

  public void createWorkLog() {
    this.workLog = WorkLog.createWorkLog(WorkType.CHECKIN, this);
  }

  public boolean isCompleted() {
    return this.status == MatchingStatus.CONFIRMED;
  }
}