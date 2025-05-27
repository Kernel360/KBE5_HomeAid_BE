package com.homeaid.domain;


import com.homeaid.serviceoption.domain.ServiceSubOption;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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


}