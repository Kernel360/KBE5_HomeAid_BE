package com.homeaid.domain;


import com.homeaid.serviceoption.domain.ServiceOption;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String serviceOptionName;

  private Integer basePrice;

  @Setter
  @OneToOne(fetch = FetchType.LAZY)
  private Reservation reservation;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdDate;

  @LastModifiedDate
  private LocalDateTime modifiedDate;

  private LocalDateTime deletedDate;

  public ReservationItem(ServiceOption serviceOption) {
    this.serviceOptionName = serviceOption.getName();
    this.basePrice = serviceOption.getPrice();
  }

  public void updateItem(ServiceOption serviceOption) {
    this.serviceOptionName = serviceOption.getName();
    this.basePrice = serviceOption.getPrice();
  }

  public void softDelete() {
    this.deletedDate = LocalDateTime.now();
  }


}