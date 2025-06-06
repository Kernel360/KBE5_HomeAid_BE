package com.homeaid.repository;

import com.homeaid.domain.ManagerAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerAvailabilityRepository extends JpaRepository<ManagerAvailability, Long> {

}
