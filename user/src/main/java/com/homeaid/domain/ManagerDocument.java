package com.homeaid.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Entity
@NoArgsConstructor
@Table(name = "manager_document")
public class ManagerDocument {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String documentUrl;

  private String documentS3Key;

  @CreatedDate
  private LocalDateTime uploadedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "manager_id")
  private Manager manager;

  @Builder
  public ManagerDocument(String documentUrl, String documentS3Key, Manager manager) {
    this.documentUrl = documentUrl;
    this.documentS3Key = documentS3Key;
    this.manager = manager;
  }


}
