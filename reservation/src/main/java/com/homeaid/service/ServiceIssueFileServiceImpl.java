package com.homeaid.service;

import com.homeaid.common.enumerate.DocumentType;
import com.homeaid.common.response.FileUploadResult;
import com.homeaid.domain.ServiceIssue;
import com.homeaid.domain.ServiceIssueImage;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.ErrorCode;
import com.homeaid.util.S3Service;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceIssueFileServiceImpl implements ServiceIssueFileService {

  private final S3Service s3Service;

  @Override
  public void uploadFiles(ServiceIssue serviceIssue, List<MultipartFile> files) {
    if (files == null || files.isEmpty()) {
      return;
    }

    for (MultipartFile file : files) {
      FileUploadResult result;
      try {
        result = s3Service.uploadFile(DocumentType.ISSUE_IMAGE, "SERVICE_ISSUES/", file);
      } catch (IOException e) {
        log.error("파일 업로드 실패 - 파일명: {}, 오류: {}", file.getOriginalFilename(), e.getMessage());
        throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
      }

      ServiceIssueImage image = ServiceIssueImage.builder()
          .originalName(file.getOriginalFilename())
          .s3Key(result.getS3Key())
          .url(result.getUrl())
          .serviceIssue(serviceIssue)
          .build();

      serviceIssue.addImage(image);
    }
  }

  @Override
  public void updateFiles(ServiceIssue serviceIssue, List<MultipartFile> files,
      List<Long> deleteImageIds) {
    log.info("📌 삭제 요청된 이미지 ID들: {}", deleteImageIds);
    log.info("🎯 현재 서비스 이슈의 이미지 ID들: {}",
        serviceIssue.getImages().stream().map(ServiceIssueImage::getId).toList());

    List<ServiceIssueImage> toRemove = serviceIssue.getImages().stream()
        .filter(img -> deleteImageIds != null && deleteImageIds.contains(img.getId()))
        .toList();

    log.info("🗑️ 삭제 대상 이미지 ID들: {}",
        toRemove.stream().map(ServiceIssueImage::getId).toList());

    toRemove.forEach(img -> s3Service.deleteFile(img.getS3Key()));
    serviceIssue.getImages().removeAll(toRemove);

    uploadFiles(serviceIssue, files);
  }

  @Override
  public void deleteFiles(ServiceIssue serviceIssue) {
    List<ServiceIssueImage> images = serviceIssue.getImages();
    if (images == null || images.isEmpty()) {
      return;
    }

    for (ServiceIssueImage image : images) {
      String s3Key = image.getS3Key();
      s3Service.deleteFile(s3Key);
    }
    images.clear();
  }


}
