package com.example.homeaid.manager.entity;

import com.example.homeaid.global.common.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "manager")
public class Manager extends User {

  private String career;

  private String experience;

  private String profileImage;

  private String documentUrl;

  private boolean verified = false;

  @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ManagerAvailability> availabilityList = new ArrayList<>();

  private LocalDateTime verifiedAt;

}
