package com.homeaid.repository;

import com.homeaid.domain.ManagerServiceOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerServiceOptionRepository extends JpaRepository<ManagerServiceOption, Long> {

}
