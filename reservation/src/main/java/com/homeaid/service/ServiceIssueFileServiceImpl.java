package com.homeaid.service;

import com.homeaid.common.enumerate.DocumentType;
import com.homeaid.common.response.FileUploadResult;
import com.homeaid.domain.ServiceIssue;
import com.homeaid.domain.ServiceIssueImage;
import com.homeaid.util.S3Service;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ServiceIssueFileServiceImpl implements ServiceIssueFileService {

  private final S3Service s3Service;

  @Override
  public void uploadFiles(ServiceIssue serviceIssue, List<MultipartFile> files) {
    if (files == null || files.isEmpty()) return;

    for (MultipartFile file : files) {
      FileUploadResult result;
      try {
        result = s3Service.uploadFile(DocumentType.ISSUE_IMAGE, "SERVICE_ISSUES/", file);
      } catch (IOException e) {
        throw new RuntimeException("파일 업로드 실패", e);
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
  public void updateFiles(ServiceIssue serviceIssue, List<MultipartFile> files) {
    deleteFiles(serviceIssue);
    uploadFiles(serviceIssue, files);
  }

  @Override
  public void deleteFiles(ServiceIssue serviceIssue) {
    List<ServiceIssueImage> images = serviceIssue.getImages();
    if (images == null || images.isEmpty()) return;

    for (ServiceIssueImage image : images) {
      String s3Key = image.getS3Key();
      s3Service.deleteFile(s3Key);
    }
    images.clear();
  }
}
