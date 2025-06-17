package com.homeaid.domain;


import com.homeaid.domain.enumerate.GenderType;
import com.homeaid.domain.enumerate.ManagerStatus;
import com.homeaid.domain.enumerate.UserRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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

  @Column(name = "profile_image_url")
  private String profileImageUrl;

  @Column(name = "profile_image_s3_key") // 삭제를 위한 S3 키
  private String profileImageS3Key;

  @Setter
  private String documentUrl;

  private Boolean verified = false;

  @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ManagerAvailability> availabilityList = new ArrayList<>();

  private LocalDateTime verifiedAt;

  @Enumerated(EnumType.STRING)
  private ManagerStatus status = ManagerStatus.PENDING;

  @Builder
  public Manager(String email, String password, String name, String phone, LocalDate birth,
      GenderType gender, String career, String experience, String profileImageUrl, String profileImageS3Key) {
    super(email, password, name, phone, birth, gender, UserRole.MANAGER);
    this.career = career;
    this.experience = experience;
    this.profileImageUrl = profileImageUrl;
    this.profileImageS3Key = profileImageS3Key;
  }

  public void approve() {
    this.verified = true;
    this.verifiedAt = LocalDateTime.now();
  }

  public void changeStatus(ManagerStatus newStatus) {
    this.status = newStatus;
  }

  public void updateProfileImage(String imageUrl, String s3Key) {
    this.profileImageUrl = imageUrl;
    this.profileImageS3Key = s3Key;
  }
}
