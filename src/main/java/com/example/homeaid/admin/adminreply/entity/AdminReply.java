package com.example.homeaid.admin.adminreply.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin_answer")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminReply {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "admin_id", nullable = false)
  private Long adminId;

  @Enumerated(EnumType.STRING)
  @Column(name = "post_type", nullable = false)
  private PostType postType; // CUSTOMER 또는 MANAGER

  @Column(name = "post_id")
  private Long postId;

  @Column(name = "content", nullable = false)
  private String content;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updateAt;


  public void createReply(PostType postType, Long postId, Long adminId) {
    if (postType != null) this.postType = postType;
    if (postId != null) this.postId = postId;
    this.adminId = adminId;
    this.createdAt = LocalDateTime.now();
    this.updateAt = LocalDateTime.now();
  }

  public void updateReply(String content) {
    if (content != null) {
      this.content = content;
      this.updateAt = LocalDateTime.now();
    }
  }

}
