package com.homeaid.service;

import com.homeaid.domain.Customer;
import com.homeaid.domain.CustomerAddress;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.repository.CustomerAddressRepository;
import com.homeaid.repository.CustomerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CustomerAddressServiceImpl implements CustomerAddressService {

  private final CustomerAddressRepository customerAddressRepository;
  private final CustomerRepository customerRepository;
  private static final int MAX_ADDRESS_COUNT = 10;


  @Override
  @Transactional(readOnly = true)
  public List<CustomerAddress> getSavedAddresses(Long customerId) {

    return customerAddressRepository.findByCustomerIdOrderByIdDesc(
        customerId);
  }


  @Override
  @Transactional
  public CustomerAddress saveAddress(Long customerId, CustomerAddress customerAddress) {

    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new CustomException(UserErrorCode.CUSTOMER_NOT_FOUND));

    validateAddressLimit(customerId);

    validateDuplicateAddressAlias(customerId, customerAddress.getAlias());

    customer.addAddress(customerAddress);

    return customerAddressRepository.save(customerAddress);
  }


  @Override
  @Transactional
  public CustomerAddress updateAddress(Long customerId, Long addressId,
      CustomerAddress updatedAddress) {

    CustomerAddress originCustomerAddress = customerAddressRepository.findByCustomerIdAndId(
        customerId, addressId);

    if (originCustomerAddress == null) {
      throw new CustomException(UserErrorCode.ADDRESS_NOT_FOUND);
    }

    originCustomerAddress.updateAddressInfo(updatedAddress);

    return originCustomerAddress;
  }


  @Override
  @Transactional
  public void deleteAddress(Long customerId, Long addressId) {

    CustomerAddress address = customerAddressRepository.findByCustomerIdAndId(customerId,
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
    if (customerAddressRepository.countByCustomerId(customerId) >= MAX_ADDRESS_COUNT) {
      throw new CustomException(UserErrorCode.ADDRESS_LIMIT_EXCEEDED);
    }
  }

}
