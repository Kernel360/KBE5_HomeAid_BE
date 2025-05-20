package com.example.homeaid.customerboard.repository;

import com.example.homeaid.customerboard.entity.CustomerBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerBoardRepository extends JpaRepository<CustomerBoard,Long> {

  Page<CustomerBoard> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

}
