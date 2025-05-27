package com.homeaid.serviceoption.repository;


import com.homeaid.serviceoption.domain.ServiceSubOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceSubOptionRepository extends JpaRepository<ServiceSubOption, Long> {

}
