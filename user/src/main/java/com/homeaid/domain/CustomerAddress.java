package com.homeaid.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "customer_address")
public class CustomerAddress {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id")
  @JsonIgnore
  private Customer customer;

  private String address;

  private String addressDetail;

  private Double latitude;

  private Double longitude;

  @Setter
  private String alias;

  @Builder
  public CustomerAddress(String address, String addressDetail, Double latitude, Double longitude) {
    this.address = address;
    this.addressDetail = addressDetail;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public void updateAddressInfo(CustomerAddress updatedCustomerAddress) {
    this.addressDetail = updatedCustomerAddress.getAddressDetail();
  }
}