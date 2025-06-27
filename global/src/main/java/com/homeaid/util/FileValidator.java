package com.homeaid.util;

import com.homeaid.exception.CustomException;
import com.homeaid.exception.ErrorCode;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileValidator {

  private static final List<String> SUPPORTED_IMAGE_TYPES = List.of(
      "image/jpeg", "image/png", "image/jpg", "image/webp", "image/avif"
  );

  private static final List<String> SUPPORTED_DOCUMENT_TYPES = List.of(
      "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
  );

  public static void validateImageFile(MultipartFile file) {
    String contentType = file.getContentType();
    if (contentType == null || !SUPPORTED_IMAGE_TYPES.contains(contentType)) {
      throw new CustomException(ErrorCode.INVALID_IMAGE_TYPE);
    }
  }

  public static void validateDocumentFile(MultipartFile file) {
    String contentType = file.getContentType();
    if (contentType == null || !SUPPORTED_DOCUMENT_TYPES.contains(contentType)) {
      throw new CustomException(ErrorCode.INVALID_DOCUMENT_TYPE);
    }
  }
}
