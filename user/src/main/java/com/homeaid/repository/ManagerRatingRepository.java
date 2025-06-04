package com.homeaid.repository;

import com.homeaid.domain.ManagerRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRatingRepository extends JpaRepository<ManagerRating, Long> {

}
