package com.homeaid.domain;


import com.homeaid.domain.enumerate.GenderType;
import com.homeaid.domain.enumerate.UserRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "manager")
public class Manager extends User {

  private String career;

  private String experience;

  private String profileImage;

  @Setter
  private String documentUrl;

  private Boolean verified = false;

  @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ManagerAvailability> availabilityList = new ArrayList<>();

  private LocalDateTime verifiedAt;

  @Builder
  public Manager(String email, String password, String name, String phone, LocalDate birth,
      GenderType gender, String career, String experience, String profileImage) {
    super(email, password, name, phone, birth, gender, UserRole.MANAGER);
    this.career = career;
    this.experience = experience;
    this.profileImage = profileImage;
  }

  public void approve() {
    this.verified = true;
    this.verifiedAt = LocalDateTime.now();
  }
}
