package com.homeaid.repository;

import com.homeaid.domain.CustomerRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRatingRepository extends JpaRepository<CustomerRating, Long> {

}
