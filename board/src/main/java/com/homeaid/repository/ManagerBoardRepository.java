package com.homeaid.repository;


import com.homeaid.domain.ManagerBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerBoardRepository extends JpaRepository<ManagerBoard, Long> {

}
