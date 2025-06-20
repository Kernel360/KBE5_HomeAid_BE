package com.homeaid.common.response;

import com.homeaid.common.enumerate.DocumentType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FileUploadResult {
  private final DocumentType documentType;
  private final String s3Key;
  private final String url;


  @Builder
  public FileUploadResult(DocumentType documentType, String s3Key, String url) {
    this.documentType = documentType;
    this.s3Key = s3Key;
    this.url = url;
  }

}