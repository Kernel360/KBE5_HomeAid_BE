package com.homeaid.domain;



import com.homeaid.domain.enumerate.GenderType;
import com.homeaid.domain.enumerate.ManagerStatus;
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


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "manager")
public class Manager extends User {

  private String career;

  private String experience;

  @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ManagerDocument> documents = new ArrayList<>();

  private Boolean verified = false;

  @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ManagerAvailability> availabilityList = new ArrayList<>();

  private LocalDateTime verifiedAt;

  @Enumerated(EnumType.STRING)
  private ManagerStatus status = ManagerStatus.PENDING;

  private String rejectionReason; // 반려 사유 필드 추가

  @Builder
  public Manager(String email, String password, String name, String phone, LocalDate birth,
      GenderType gender, String career, String experience) {
    super(email, password, name, phone, birth, gender, UserRole.MANAGER);
    this.career = career;
    this.experience = experience;
  }

  public void approve() {
    this.verified = true;
    this.verifiedAt = LocalDateTime.now();
  }

  public void changeStatus(ManagerStatus newStatus) {
    this.status = newStatus;
  }

  public void reject(String reason) {
    this.status = ManagerStatus.REJECTED;
    this.rejectionReason = reason;
  }
}
