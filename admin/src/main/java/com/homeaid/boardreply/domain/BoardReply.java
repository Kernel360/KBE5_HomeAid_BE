package com.homeaid.boardreply.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "admin_reply")
@EntityListeners(AuditingEntityListener.class)
public class BoardReply {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "admin_id", nullable = false)
  private Long adminId;

  @Column(name = "board_id", nullable = false)
  private Long boardId;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private LocalDateTime updatedAt;

  public void createReply(Long boardId, Long adminId) {
    if (boardId != null) this.boardId = boardId;
    this.adminId = adminId;
  }

  public BoardReply updateReply(String content) {
    if (content != null && !content.trim().isEmpty()) {
      this.content = content;
    }
    return null;
  }
}