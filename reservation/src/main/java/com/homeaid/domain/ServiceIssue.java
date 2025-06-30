package com.homeaid.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "service_issue")
@EntityListeners(AuditingEntityListener.class)
public class ServiceIssue {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reservation_id")
  private Reservation reservation;

  private String content;

  @OneToMany(mappedBy = "serviceIssue", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ServiceIssueImage> images = new ArrayList<>();

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @Builder
  public ServiceIssue(Reservation reservation, String content) {
    this.reservation = reservation;
    this.content = content;
  }


  public void updateIssue(String content) {
    if (content != null && !content.trim().isEmpty()) {
      this.content = content.trim();
    }
  }

  public void addImage(ServiceIssueImage image) {
    this.images.add(image);
    image.setServiceIssue(this);
  }
}
