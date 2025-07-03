package com.homeaid.common.response;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileDownloadResult {

  private byte[] content;           // 파일 바이너리
  private String originalFileName;  // 파일 원본 이름
  private String contentType;       // MIME 타입 (ex. image/jpeg, application/pdf 등)
  private long contentLength;       // 바이트 단위 크기
  private String fileExtension;     // 파일 확장자
  private Date lastModified;        // 마지막 수정일자
  private String s3Key;             // S3 파일 키

}