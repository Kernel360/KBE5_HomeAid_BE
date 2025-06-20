package com.homeaid.user.repository;

import com.homeaid.domain.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminManagerRepository extends JpaRepository<Manager, Long> {

}
