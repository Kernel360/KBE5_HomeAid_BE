package com.homeaid.service;

import com.homeaid.domain.Customer;
import com.homeaid.domain.CustomerRating;
import com.homeaid.domain.Manager;
import com.homeaid.domain.ManagerRating;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.repository.CustomerRatingRepository;
import com.homeaid.repository.CustomerRepository;
import com.homeaid.repository.ManagerRatingRepository;
import com.homeaid.repository.ManagerRepository;
import com.homeaid.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRatingUpdateServiceImpl implements UserRatingUpdateService {

  private final ReviewRepository reviewRepository;
  private final CustomerRepository customerRepository;
  private final ManagerRepository managerRepository;
  private final CustomerRatingRepository customerRatingRepository;
  private final ManagerRatingRepository managerRatingRepository;

  @Transactional
  @Override
  public void updateRating(Long targetId, UserRole writerRole) {
    Object[] rating = reviewRepository.getReviewStatisticsByTargetId(targetId);

    int count = ((Number) rating[0]).intValue();
    double average = ((Number) rating[1]).doubleValue();

    // 작성자: CUSTOMER -> MANAGER update / 작성자: MANAGER -> CUSTOMER update
    if (writerRole == UserRole.CUSTOMER) {
      updateManagerRating(targetId, count, average);
    } else if (writerRole == UserRole.MANAGER) {
      updateCustomerRating(targetId, count, average);
    }
  }

  private void updateManagerRating(Long managerId, int count, double average) {
    Manager manager = managerRepository.getReferenceById(managerId);
    ManagerRating rating = managerRatingRepository.findById(managerId)
        .orElseGet(() -> new ManagerRating(manager));
    rating.updateRating(count, average);
    managerRatingRepository.save(rating);
  }

  private void updateCustomerRating(Long customerId, int count, double average) {
    Customer customer = customerRepository.getReferenceById(customerId);
    CustomerRating rating = customerRatingRepository.findById(customerId)
        .orElseGet(() -> new CustomerRating(customer));
    rating.updateRating(count, average);
    customerRatingRepository.save(rating);
  }
}
