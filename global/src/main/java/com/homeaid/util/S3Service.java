package com.homeaid.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class S3Service {

  private final AmazonS3 amazonS3;

  @Value("${spring.cloud.aws.s3.bucket}")
  private String bucket;

  private S3Service(AmazonS3 amazonS3) {
    this.amazonS3 = amazonS3;
  }

  // S3에 파일 업로드
  public String uploadFile(MultipartFile file) throws IOException {
    String fileName = UUID.randomUUID()+ " " + file.getOriginalFilename();

    // 메타데이터 설정
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType(file.getContentType());
    metadata.setContentLength(file.getSize());

    // S3에 파일 업로드 요청 생성
    PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata);

    // S3에 파일 업로드
    amazonS3.putObject(putObjectRequest);

    return getPublicUrl(fileName);
  }

  private String getPublicUrl(String fileName) {
    return String.format("https://s3-%s.amazonaws.com/%s", bucket, amazonS3.getRegionName(), fileName);
  }

}
