package com.homeaid.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "service_issue_image")
public class ServiceIssueImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String originalName;

  @Column(nullable = false, name = "s3_key")
  private String s3Key;

  @Column(nullable = false)
  private String url;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "service_issue_id")
  private ServiceIssue serviceIssue;

  @Builder
  public ServiceIssueImage(String originalName, String s3Key, String url, ServiceIssue serviceIssue) {
    this.originalName = originalName;
    this.s3Key = s3Key;
    this.url = url;
    this.serviceIssue = serviceIssue;
  }
}
