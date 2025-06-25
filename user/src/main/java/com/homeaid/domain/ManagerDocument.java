package com.homeaid.domain;

import com.homeaid.common.enumerate.DocumentType;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "manager_document")
@EntityListeners(AuditingEntityListener.class)
public class ManagerDocument {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "document_type")
  private DocumentType documentType;

  @Column(name = "document_s3_key")
  private String documentS3Key;

  @Column(name = "document_url")
  private String documentUrl; // 신분증, 범죄 경력 조회서, 보건증 및 건강검진서

  @CreatedDate
  private LocalDateTime uploadedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "manager_id")
  private Manager manager;

  @Builder
  public ManagerDocument(DocumentType documentType, String documentS3Key, String documentUrl,
      Manager manager) {
    this.documentType = documentType;
    this.documentS3Key = documentS3Key;
    this.documentUrl = documentUrl;
    this.manager = manager;
  }
}
