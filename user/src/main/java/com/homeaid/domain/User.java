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
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
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

  @Column
  private String password;

  @Column(nullable = false)
  private String name;

  @Column(unique = true)
  private String phone;

  @Column
  private LocalDate birth;

  @Enumerated(EnumType.STRING)
  private GenderType gender;

  @Enumerated(EnumType.STRING)
  private UserRole role;

  @Column(name = "profile_image_url")
  private String profileImageUrl;

  @Column(name = "profile_image_s3_key") // 삭제를 위한 S3 키
  private String profileImageS3Key;

  private String provider; // ex: google

  private String providerId; // 구글 로그인 유저의 고유 ID

  @Column(nullable = false)
  private Boolean deleted = false;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  public void delete() {
    this.deleted = true;
  }

  public boolean isDeleted() {
    return Boolean.TRUE.equals(this.deleted);
  }

  @Setter
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

  public User (UserRole role, String phone, LocalDate birth, GenderType gender) {
    this.role = role;
    this.phone = phone;
    this.birth = birth;
    this.gender = gender;
  }

  public static User createOAuth2User(String provider, String providerId, String email, String name,
      String imageUrl, UserRole userRole) {
    User user = new User();
    user.provider = provider;
    user.providerId = providerId;
    user.email = email;
    user.name = name;
    user.profileImageUrl = imageUrl;
    user.role = userRole;
    user.password = RandomStringUtils.randomAlphanumeric(20);
    return user;
  }

  public void updateInfo(String name, String email, String phone) {
    this.name = name;
    this.email = email;
  }

  public void profileImage(String imageUrl, String s3Key) {
    this.profileImageUrl = imageUrl;
    this.profileImageS3Key = s3Key;
  }

  public void additionalOAuthInfo(UserRole role, String phone, LocalDate birth, GenderType gender) {
    this.role = role;
    this.phone = phone;
    this.birth = birth;
    this.gender = gender;
  }

  public void updateOAuthProfile(String name, String picture) {
    this.name = name;
    this.profileImageUrl = picture;
  }

  public boolean isProfileComplete() {
    return this.phone != null && this.birth != null;
  }
}
