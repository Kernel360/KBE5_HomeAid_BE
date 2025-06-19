package com.homeaid.user.service;

import com.homeaid.domain.Customer;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.user.dto.request.AdminCustomerSearchRequestDto;
import com.homeaid.user.repository.AdminCustomerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class AdminCustomerServiceImpl implements AdminCustomerService {

  private final AdminCustomerRepository adminCustomerRepository;

  @Override
  @Transactional(readOnly = true)
  public Page<Customer> searchCustomers(AdminCustomerSearchRequestDto dto, Pageable pageable) {
    Long userId = dto.getUserId();
    String name = dto.getName();
    String phone = dto.getPhone();

    if (userId != null) {
      Customer customer = adminCustomerRepository.findById(userId)
          .filter(c -> !c.isDeleted())
          .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
      return new PageImpl<>(List.of(customer), pageable, 1);
    }

    if (name != null && !name.isBlank()) {
      return adminCustomerRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    if (phone != null && !phone.isBlank()) {
      return adminCustomerRepository.findByPhoneContaining(phone, pageable);
    }

    // 조건 없으면 전체 조회
    return adminCustomerRepository.findAll(pageable);
  }
}
