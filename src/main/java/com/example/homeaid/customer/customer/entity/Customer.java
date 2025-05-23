package com.example.homeaid.customer.customer.entity;


import com.example.homeaid.global.common.entity.User;
import com.example.homeaid.global.common.entity.enumerate.GenderType;
import com.example.homeaid.global.common.entity.enumerate.UserRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "customer")
public class Customer extends User {

  @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CustomerAddress> addressList = new ArrayList<>();

  public void addAddress(CustomerAddress address) {
    addressList.add(address);
    address.setCustomer(this);
  }

  @Builder
  public Customer(String email, String password, String name, String phone,
      LocalDate birth, GenderType gender, UserRole role) {
    super(email, password, name, phone, birth, gender, role);
  }
}
