package com.homeaid.service;

import com.homeaid.domain.CustomerAddress;
import java.util.List;

public interface CustomerAddressService {

  List<CustomerAddress> getSavedAddresses(Long customerId);

  CustomerAddress saveAddress(Long customerId, CustomerAddress customerAddress);

  CustomerAddress updateAddress(Long customerId, Long addressId, CustomerAddress customerAddress);


    void deleteAddress(Long customerId, Long addressId);

}
