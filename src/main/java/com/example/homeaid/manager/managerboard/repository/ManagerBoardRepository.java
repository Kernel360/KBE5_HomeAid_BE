package com.example.homeaid.manager.managerboard.repository;

import com.example.homeaid.manager.managerboard.entity.ManagerBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerBoardRepository extends JpaRepository<ManagerBoard, Long> {

}
