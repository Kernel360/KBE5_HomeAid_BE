package com.homeaid.domain;


import com.homeaid.domain.enumerate.GenderType;
import com.homeaid.domain.enumerate.UserRole;
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

  // 단일 주소 입력(회원가입)
  @Builder(builderMethodName = "addSingleAddress")
  public Customer(String email, String password, String name, String phone,
      LocalDate birth, GenderType gender, CustomerAddress address) {

    super(email, password, name, phone, birth, gender, UserRole.CUSTOMER);

    if (address != null) {
      this.addressList.add(address);
    }
  }


  // 복수 주소 입력
  @Builder(builderMethodName = "addMultipleAddresses")
  public Customer(String email, String password, String name, String phone,
      LocalDate birth, GenderType gender, List<CustomerAddress> addresses) {

    super(email, password, name, phone, birth, gender, UserRole.CUSTOMER);

    if (addresses != null && !addresses.isEmpty()) {
      this.addressList.addAll(addresses);
    }
  }


  public void addAddress(CustomerAddress address) {
    addressList.add(address);
    address.setCustomer(this);
  }


  public void removeAddress(CustomerAddress address) {
    if (address != null) {
      addressList.remove(address);
      address.setCustomer(null);
    }
  }

}
