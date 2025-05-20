package com.example.homeaid.managerboard.repository;

import com.example.homeaid.managerboard.entity.ManagerBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerBoardRepository extends JpaRepository<ManagerBoard, Long> {

}
