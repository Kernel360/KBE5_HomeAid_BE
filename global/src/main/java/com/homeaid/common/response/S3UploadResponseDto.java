package com.homeaid.common.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class S3UploadResponseDto {
  private String s3Key;
  private String url;

  public S3UploadResponseDto(String s3Key, String url) {
    this.s3Key = s3Key;
    this.url = url;
  }

}