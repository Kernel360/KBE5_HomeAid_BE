package com.homeaid.user.service;

import com.homeaid.domain.Customer;
import com.homeaid.user.dto.request.AdminCustomerSearchRequestDto;
import com.homeaid.user.repository.AdminCustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminCustomerServiceImpl implements AdminCustomerService {

  private final AdminCustomerRepository adminCustomerRepository;

  @Override
  @Transactional(readOnly = true)
  public Page<Customer> getAllCustomers(Pageable pageable) {
    return adminCustomerRepository.findAll(pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Customer> searchCustomers(AdminCustomerSearchRequestDto dto, Pageable pageable) {
    Long userId = dto.getUserId();
    String name = dto.getName();
    String phone = dto.getPhone();

    // 1. ID로 단건 조회
//    if (userId != null) {
//      Customer customer = adminCustomerRepository.findById(userId).orElse(null);
//      if (customer != null && !customer.isDeleted()) {
//        return new PageImpl<>(List.of(customer), pageable, 1);
//      } else {
//        return Page.empty(pageable);
//      }
//    }

    if (userId != null) {
      Customer customer = adminCustomerRepository.findById(userId).orElse(null);
    }

    // 2. 이름만 있는 경우
    if (name != null && !name.isBlank()) {
      return adminCustomerRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    // 3. 전화번호만 있는 경우
    if (phone != null && !phone.isBlank()) {
      return adminCustomerRepository.findByPhoneContaining(phone, pageable);
    }

    // 4. 조건 없으면 전체 조회
    return adminCustomerRepository.findAll(pageable);
  }


}
