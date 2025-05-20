package com.example.homeaid.manager.entity;

import com.example.homeaid.global.common.entity.User;
import com.example.homeaid.manager.entity.enumerated.GenderType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Manager extends User {

  private String career;

  private String experience;

  @Enumerated(EnumType.STRING)
  private GenderType gender;

  private String profileImage;

  private String documentUrl;

  private boolean verified = false;

  @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ManagerAvailability> availabilityList = new ArrayList<>();

  private LocalDateTime verifiedAt;

}
