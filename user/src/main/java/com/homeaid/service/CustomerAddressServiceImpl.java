package com.homeaid.service;

import com.homeaid.domain.Customer;
import com.homeaid.domain.CustomerAddress;
import com.homeaid.dto.response.CustomerAddressResponseDto;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.repository.CustomerAddressRepository;
import com.homeaid.repository.CustomerRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerAddressServiceImpl implements CustomerAddressService {

  private final CustomerAddressRepository customerAddressRepository;
  private final CustomerRepository customerRepository;
  private static final int MAX_ADDRESS_COUNT = 10;

  // 고객의 저장된 주소 목록 조회
  @Override
  public List<CustomerAddressResponseDto> getSavedAddresses(Long customerId) {

    // 고객 존재 여부 확인
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new CustomException(UserErrorCode.CUSTOMER_NOT_FOUND));

    List<CustomerAddress> addresses = customerAddressRepository.findByCustomerIdOrderByIdDesc(
        customerId);

    return addresses.stream()
        .map(CustomerAddressResponseDto::toDto)
        .collect(Collectors.toList());
  }


  @Transactional
  @Override
  public CustomerAddress saveAddress(Long customerId, CustomerAddress customerAddress) {

    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new CustomException(UserErrorCode.CUSTOMER_NOT_FOUND));

    validateAddressLimit(customerId);

    validateDuplicateAddressAlias(customerId, customerAddress.getAlias());

    customer.addAddress(customerAddress);

    return customerAddressRepository.save(customerAddress);
  }

  /**
   * 주소 수정
   */
  @Override
  @Transactional
  public CustomerAddress updateAddress(Long customerId, Long addressId,
      CustomerAddress requestDto) {

    // 수정할 주소 조회 및 권한 확인
    CustomerAddress customerAddress = customerAddressRepository.findByCustomerIdAndAddressId(
        customerId, addressId);

    if (customerAddress == null) {
      throw new CustomException(UserErrorCode.ADDRESS_NOT_FOUND);
    }

    // 중복 주소 확인 (수정하려는 주소가 다른 저장된 주소와 중복되는지 확인)
    validateDuplicateAddressForUpdate(customerId, addressId, requestDto);

    // 주소 정보 업데이트
    customerAddress.updateAddressInfo(
        requestDto.getAddress(),
        requestDto.getAddressDetail(),
        requestDto.getLatitude(),
        requestDto.getLongitude()
    );

    CustomerAddress savedAddress = customerAddressRepository.save(customerAddress);

    return savedAddress;
  }

  // 주소 삭제
  @Transactional
  @Override
  public void deleteAddress(Long customerId, Long addressId) {

    CustomerAddress address = customerAddressRepository.findByCustomerIdAndAddressId(customerId,
        addressId);

    if (address == null) {
      throw new CustomException(UserErrorCode.ADDRESS_NOT_FOUND);
    }

    customerAddressRepository.delete(address);
  }


  private void validateDuplicateAddressAlias(Long customerId, String alias) {
    if (customerAddressRepository.existsByCustomerIdAndAlias(customerId, alias)) {
      throw new CustomException(UserErrorCode.DUPLICATE_ADDRESS_ALIAS);
    }
  }


  private void validateAddressLimit(Long customerId) {
    if (customerAddressRepository.countByCustomerId(customerId) > MAX_ADDRESS_COUNT) {
      throw new CustomException(UserErrorCode.ADDRESS_LIMIT_EXCEEDED);
    }
  }

  // 수정용 중복 주소 검증
  private void validateDuplicateAddressForUpdate(Long customerId, Long addressId,
      CustomerAddress requestDto) {
    List<CustomerAddress> existingAddresses = customerAddressRepository.findByCustomerIdOrderByIdDesc(
        customerId);

    boolean isDuplicate = existingAddresses.stream()
        .filter(addr -> !addr.getId().equals(addressId)) // 현재 수정하는 주소는 제외
        .anyMatch(addr -> addr.getAddress().equals(requestDto.getAddress())
            && (addr.getAddressDetail() != null ? addr.getAddressDetail()
            .equals(requestDto.getAddressDetail()) : requestDto.getAddressDetail() == null));

    if (isDuplicate) {
      throw new CustomException(UserErrorCode.DUPLICATE_ADDRESS);
    }
  }
}
