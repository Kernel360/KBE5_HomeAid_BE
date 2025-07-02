package com.homeaid.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.homeaid.common.enumerate.DocumentType;
import com.homeaid.common.response.FileUploadResult;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.ErrorCode;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
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

  // 파일 업로드
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
    } catch (IOException e) {
      throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
    }
    log.info("파일 업로드 성공 - key: {}, url: {}", fileName, getPublicUrl(fileName));
    return new FileUploadResult(documentType, fileName, getPublicUrl(fileName));
  }

  // 파일 삭제
  public void deleteFile(String fileKey) throws FileNotFoundException {
    // S3 파일 유무 확인
    validateFileExists(fileKey);

    try {
      amazonS3.deleteObject(bucket, fileKey);
      log.debug("파일 삭제 성공 - key: {}", fileKey);

    } catch (Exception e) {
      log.error("파일 삭제 실패 - key: {}", fileKey, e);
      throw new CustomException(ErrorCode.FILE_DELETE_ERROR);
    }
  }

  // 파일 다운로드
  public byte[] getFile(String fileKey) throws FileNotFoundException {

    // S3 파일 유무 확인
    validateFileExists(fileKey);

    S3Object s3Object = amazonS3.getObject(bucket, fileKey);
    S3ObjectInputStream s3ObjectContent = s3Object.getObjectContent();

    try {
      log.debug("파일 다운로드 - key: {}", fileKey);
      return IOUtils.toByteArray(s3ObjectContent);

    } catch (IOException e) {
      log.error("파일 다운로드 실패 - key: {}", fileKey, e);
      throw new FileNotFoundException();
    }
  }

  // 파일명 생성
  private String createFileName(MultipartFile file) {
    String originalName = file.getOriginalFilename();
    String safeName = Objects.requireNonNull(originalName).replaceAll("[^a-zA-Z0-9.\\-]", "_");
    return UUID.randomUUID() + "_" + safeName;
  }

  // URL 생성
  private String getPublicUrl(String fileName) {
    return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, amazonS3.getRegionName(),
        fileName);
  }

  // 파일 유효성 검증
  private void validateFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      log.error("파일이 존재하지 않음 - file: {}", file);
      throw new CustomException(ErrorCode.FILE_EMPTY);
    }
  }

  // S3에서 파일 존재 여부 검증
  private void validateFileExists(String fileKey) throws FileNotFoundException {
    if (!amazonS3.doesObjectExist(bucket, fileKey)) {
      throw new FileNotFoundException();
    }
  }
}
