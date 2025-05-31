package com.homeaid.domain;

import com.homeaid.domain.enumerate.UserRole;
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
@Builder
@Table(name = "user_board")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserBoard {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false, length = 255)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  private UserRole role;

  private boolean isAnswered; // 답변 등록 유무

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private LocalDateTime updatedAt;


  public void updateBoard(String title, String content) {
    validateUpdatePermission(); // 수정 가능 여부 검증

    if (title != null && !title.trim().isEmpty()) {
      this.title = title.trim();
    }
    if (content != null && !content.trim().isEmpty()) {
      this.content = content.trim();
    }
  }

  private void validateUpdatePermission() {
    if (this.isAnswered) {
      throw new IllegalStateException("답변이 완료된 게시글은 수정할 수 없습니다.");
    }
  }

}
