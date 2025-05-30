package com.homeaid.repository;

import com.homeaid.domain.Settlement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {
  List<Settlement> findAllByManagerId(Long managerId);
}
