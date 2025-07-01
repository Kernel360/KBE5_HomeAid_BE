package com.homeaid.service;

import com.homeaid.common.request.UploadFileParam;
import com.homeaid.common.response.FileUploadResult;
import com.homeaid.domain.Manager;
import com.homeaid.domain.ManagerDocument;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.ErrorCode;
import com.homeaid.repository.ManagerDocumentRepository;
import com.homeaid.util.S3Service;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManagerDocumentServiceImpl implements ManagerDocumentService {

  private final ManagerDocumentRepository managerDocumentRepository;
  private final S3Service s3Service;

  @Override
  public List<ManagerDocument> upload(Manager manager, List<UploadFileParam> files) {
    return files.stream()
        .filter(param -> param.file() != null && !param.file().isEmpty())
        .map(param -> {
          try {
            FileUploadResult result = s3Service.uploadFile(param.documentType(),
                param.packageName(), param.file());

            return ManagerDocument.builder()
                .documentType(param.documentType())
                .originalName(param.file().getOriginalFilename())
                .fileExtension(getFileExtension(param.file().getOriginalFilename()))
                .fileSize(param.file().getSize())
                .s3Key(result.getS3Key())
                .url(result.getUrl())
                .manager(manager)
                .build();

          } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
          }
        })
        .toList();
  }

  @Override
  public List<ManagerDocument> update(Manager manager, List<UploadFileParam> files) {
    return files.stream()
        .filter(param -> param.file() != null && !param.file().isEmpty())
        .map(param -> {
          managerDocumentRepository.findByManagerIdAndDocumentType(manager.getId(),
                  param.documentType())
              .ifPresent(existing -> {
                s3Service.deleteFile(existing.getS3Key());
                managerDocumentRepository.delete(existing);
              });

          try {
            FileUploadResult result = s3Service.uploadFile(param.documentType(),
                param.packageName(), param.file());

            return ManagerDocument.builder()
                .documentType(param.documentType())
                .originalName(param.file().getOriginalFilename())
                .fileExtension(getFileExtension(param.file().getOriginalFilename()))
                .fileSize(param.file().getSize())
                .s3Key(result.getS3Key())
                .url(result.getUrl())
                .manager(manager)
                .build();

          } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
          }
        })
        .toList();
  }


  // 파일 확장자 추출
  private String getFileExtension(String originalFilename) {
    if (originalFilename == null || !originalFilename.contains(".")) {
      return "";
    }
    return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
  }
}