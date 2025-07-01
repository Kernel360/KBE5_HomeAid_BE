package com.homeaid.dto.response;

import com.homeaid.common.enumerate.DocumentType;
import com.homeaid.domain.ManagerDocument;
import java.time.LocalDateTime;

public record ManagerDocumentDto(
    Long id,
    DocumentType documentType,
    String originalName,
    String documentUrl,
    String fileExtension,
    String fileSize,
    LocalDateTime createdAt
) {

  public static ManagerDocumentDto toDto(ManagerDocument document) {
    String formattedSize = String.format("%.2f MB", document.getFileSize() / 1024.0 / 1024.0);
    return new ManagerDocumentDto(
        document.getId(),
        document.getDocumentType(),
        document.getOriginalName(),
        document.getUrl(),
        document.getFileExtension(),
        formattedSize,
        document.getCreatedAt()
    );
  }
}
