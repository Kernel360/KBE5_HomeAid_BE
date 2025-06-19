package com.homeaid.domain;


import com.homeaid.domain.enumerate.GenderType;
import com.homeaid.domain.enumerate.UserRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Getter
@NoArgsConstructor
@Entity
@Table(name = "user")
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String name;

  @Column(unique = true, nullable = false)
  private String phone;

  private LocalDate birth;

  @Enumerated(EnumType.STRING)
  private GenderType gender;

  @Enumerated(EnumType.STRING)
  private UserRole role;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  @Column(name = "profile_image_url")
  private String profileImageUrl;

  @Column(name = "profile_image_s3_key") // 삭제를 위한 S3 키
  private String profileImageS3Key;

  @Column(nullable = false)
  private Boolean deleted = false;

  public void delete() {
    this.deleted = true;
  }

  public boolean isDeleted() {
    return Boolean.TRUE.equals(this.deleted);
  }

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private UserWithdrawalRequest withdrawalRequest;

  public User(String email, String password, String name, String phone, LocalDate birth,
      GenderType gender, UserRole role) {

    this.email = email;
    this.password = password;
    this.name = name;
    this.phone = phone;
    this.birth = birth;
    this.gender = gender;
    this.role = role;
  }

  // 토큰에 저장될 user 정보
  public User(Long userId, UserRole role) {
    this.id = userId;
    this.role = role;
  }


  public User(String email, UserRole role, String encodedPassword) {
    this.email = email;
    this.password = encodedPassword;
    this.role = role;
  }

  public void updateInfo(String name, String email, String phone) {
    this.name = name;
    this.email = email;
  }

  public void updateProfileImage(String imageUrl, String s3Key) {
    this.profileImageUrl = imageUrl;
    this.profileImageS3Key = s3Key;
  }

  public void setWithdrawalRequest(UserWithdrawalRequest request) {
    this.withdrawalRequest = request;
  }

}
