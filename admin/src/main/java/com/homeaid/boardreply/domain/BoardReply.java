package com.homeaid.boardreply.domain;

import com.homeaid.domain.User;
import com.homeaid.domain.enumerate.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "board_reply")
@EntityListeners(AuditingEntityListener.class)
public class BoardReply {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", insertable = false, updatable = false)
  private User user;

  @Column(name = "admin_id", nullable = false)
  private Long adminId;

  @Column(name = "board_id", nullable = false)
  private Long boardId;

  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserRole userRole; // 고객인지 매니저인지

  public void createReply(Long boardId, Long adminId) {
    if (boardId != null) this.boardId = boardId;
    this.adminId = adminId;
  }

  public BoardReply updateReply(String content) {
    if (content != null && !content.trim().isEmpty()) {
      this.content = content;
    }
    return this;
  }
}