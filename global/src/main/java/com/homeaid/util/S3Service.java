package com.homeaid.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.homeaid.common.enumerate.DocumentType;
import com.homeaid.common.response.FileUploadResult;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.ErrorCode;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class S3Service {

  private final AmazonS3 amazonS3;

  @Value("${spring.cloud.aws.s3.bucket}")
  private String bucket;

  private S3Service(AmazonS3 amazonS3) {
    this.amazonS3 = amazonS3;
  }

  // 단일 파일 업로드
  public FileUploadResult uploadFile(DocumentType documentType, String packageName,
      MultipartFile file) throws IOException {

    validateFile(file);

    String fileName = packageName + createFileName(file);

    // 메타데이터 설정
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType(file.getContentType());
    metadata.setContentLength(file.getSize());

    try (InputStream inputStream = file.getInputStream()) {
      // S3에 파일 업로드 요청 생성
      PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileName, inputStream,
          metadata);
      // S3에 파일 업로드
      amazonS3.putObject(putObjectRequest);
    }
    log.debug("파일 업로드 성공 - key: {}, url: {}", fileName, getPublicUrl(fileName));
    return new FileUploadResult(documentType, fileName, getPublicUrl(fileName));
  }

  // 다중 파일 업로드
  public List<FileUploadResult> uploadMultiFile(DocumentType documentType, String packageName,
      List<MultipartFile> multipartFile) {

    List<FileUploadResult> fileList = new ArrayList<>();

    for (MultipartFile file : multipartFile) {
      if (file == null || file.isEmpty()) {
        continue;
      }

      String fileName = packageName + createFileName(file);

      ObjectMetadata objectMetadata = new ObjectMetadata();
      objectMetadata.setContentLength(file.getSize());
      objectMetadata.setContentType(file.getContentType());

      try (InputStream inputStream = file.getInputStream()) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
        );
      } catch (IOException e) {
        throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
      }

      fileList.add(new FileUploadResult(documentType, fileName, getPublicUrl(fileName)));
    }

    return fileList;
  }

  // 파일 삭제
  public void deleteFile(String fileKey) {
    try {
      boolean isObjectExist = amazonS3.doesObjectExist(bucket, fileKey);

      if (isObjectExist) {

        amazonS3.deleteObject(bucket, fileKey);
      } else {
        log.error(fileKey + " 를 찾을 수 없음");
      }
    } catch (Exception e) {
      log.error("파일 삭제 실패 - key: {}", fileKey, e);
      throw new CustomException(ErrorCode.FILE_DELETE_ERROR);
    }
  }

  // 파일명 생성
  private String createFileName(MultipartFile file) {
    String originalName = file.getOriginalFilename();
    String safeName = originalName.replaceAll("[^a-zA-Z0-9.\\-]", "_");
    return UUID.randomUUID() + "_" + safeName;
  }

  // URL 생성
  private String getPublicUrl(String fileName) {
    return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, amazonS3.getRegionName(),
        fileName);
  }

  // 공용 파일 유효성 검증
  private void validateFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      log.error("파일이 존재하지 않음 - file: {}", file);
      throw new CustomException(ErrorCode.FILE_EMPTY);
    }
  }
}
