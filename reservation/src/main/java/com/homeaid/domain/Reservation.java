package com.homeaid.domain;

import static com.homeaid.domain.enumerate.ReservationStatus.REQUESTED;

import com.homeaid.domain.enumerate.ReservationStatus;
import com.homeaid.serviceoption.domain.ServiceOption;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
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
public class Reservation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDate requestedDate;

  private LocalTime requestedTime;

  private Integer totalPrice;

  private Integer duration;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private ReservationStatus status = REQUESTED;

  @Setter
  private Long managerId;

  @Setter
  @OneToOne
  @JoinColumn(name = "final_matching_id")
  private Matching finalMatching;

  private Double latitude;

  private Double longitude;

  private String address;

  private String addressDetail;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
  @OrderBy("createdDate DESC")
  private List<Matching> matchingList = new ArrayList<>();

  @Column(columnDefinition = "TEXT")
  private String customerMemo;

  // 부모 엔티티 저장이나 병합하면 자식 엔티티도 자동으로 저장이나 병합 됨.
  @OneToOne(mappedBy = "reservation", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private ReservationItem item;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdDate;

  @LastModifiedDate
  private LocalDateTime modifiedDate;

  private LocalDateTime deletedDate;

  @Builder
  public Reservation(LocalDate requestedDate, LocalTime requestedTime, Customer customer, Double latitude, Double longitude, String address, String addressDetail, Integer totalPrice, Integer duration) {
    this.requestedDate = requestedDate;
    this.requestedTime = requestedTime;
    this.customer = customer;
    this.latitude = latitude;
    this.longitude = longitude;
    this.address = address;
    this.addressDetail = addressDetail;
    this.totalPrice = totalPrice;
    this.duration = duration;
  }

  public void addItem(ServiceOption serviceOption) {
    this.item = new ReservationItem(serviceOption);
    item.setReservation(this);
    this.totalPrice = serviceOption.getPrice() * this.duration;
  }

  public void updateReservation(Reservation newReservation, int newTotalPrice,
      int newDuration) {
    this.requestedDate = newReservation.getRequestedDate();
    this.requestedTime = newReservation.getRequestedTime();
    this.totalPrice = newTotalPrice;
    this.duration = newDuration;
  }

  public void softDelete() {
    this.deletedDate = LocalDateTime.now();
    if (this.item != null) {
      this.item.softDelete();
    }
  }

  public void updateStatusCompleted() {
    this.status = ReservationStatus.COMPLETED;
  }

  public void updateStatusMatching() {
    this.status = ReservationStatus.MATCHING;
  }

  public void confirmMatching() {
    this.status = ReservationStatus.MATCHED;
  }

  public void failedMatching() {
    this.status = ReservationStatus.REQUESTED;
    this.managerId = null;
  }

  public Optional<Matching> getLatestMatching() {
    return matchingList.stream().findFirst();
  }

  public Matching createMatching(Manager manager) {
    Matching newMatching = Matching.create(manager, matchingList.size());
    newMatching.setReservation(this);
    updateStatusMatching();
    setManagerId(manager.getId());
    this.matchingList.add(newMatching);
    return newMatching;
  }

}

