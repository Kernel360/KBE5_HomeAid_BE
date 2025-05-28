package com.homeaid.serviceoption.repository;


import com.homeaid.serviceoption.domain.ServiceOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceOptionRepository extends JpaRepository<ServiceOption, Long> {

}
