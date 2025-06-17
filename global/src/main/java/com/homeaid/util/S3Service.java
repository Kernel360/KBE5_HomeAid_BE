package com.homeaid.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.ErrorCode;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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

  // 단일 파일 업로드
  public String uploadFile(MultipartFile file) throws IOException {
    String fileName = createFileName(file);

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

  // 다중 파일 업로드
  public List<String> uploadMultiFile(List<MultipartFile> multipartFile) {

    List<String> fileNameList = new ArrayList<>();

    multipartFile.forEach(file -> {
      String fileName = createFileName(file);
      ObjectMetadata objectMetadata = new ObjectMetadata();
      objectMetadata.setContentLength(file.getSize());
      objectMetadata.setContentType(file.getContentType());

      try(InputStream inputStream = file.getInputStream()) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
            .withCannedAcl(CannedAccessControlList.PublicRead));
      } catch(IOException e) {
        throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
      }

      fileNameList.add(getPublicUrl(fileName));
    });

    return fileNameList;
  }


  private String getPublicUrl(String fileName) {
    return String.format("https://s3-%s.amazonaws.com/%s", bucket, amazonS3.getRegionName(), fileName);
  }

  private String createFileName(MultipartFile file) {
    return UUID.randomUUID()+ "/" + file.getOriginalFilename();
  }
}
