package com.homeaid.repository;

import com.homeaid.domain.CustomerAddress;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {
  // 특정 고객의 저장된 주소 목록 조회
  List<CustomerAddress> findByCustomerIdOrderByIdDesc(Long customerId);

  // 특정 고객의 주소 개수 조회
  long countByCustomerId(Long customerId);

  // 특정 고객의 특정 주소 조회
  @Query("SELECT ca FROM CustomerAddress ca WHERE ca.customer.id = :customerId AND ca.id = :addressId")
  CustomerAddress findByCustomerIdAndAddressId(@Param("customerId") Long customerId, @Param("addressId") Long addressId);

}

