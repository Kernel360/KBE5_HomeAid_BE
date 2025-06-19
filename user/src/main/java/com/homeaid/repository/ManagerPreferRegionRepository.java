package com.homeaid.repository;

import com.homeaid.domain.ManagerPreferRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerPreferRegionRepository extends JpaRepository<ManagerPreferRegion, Long> {

}
