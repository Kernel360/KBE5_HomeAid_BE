package com.homeaid.dto.response;

import com.homeaid.common.enumerate.DocumentType;
import com.homeaid.domain.ManagerDocument;
import java.time.LocalDateTime;

public record ManagerDocumentDto(
    DocumentType documentType,
    String documentUrl,
    LocalDateTime uploadedAt
) {
  public static ManagerDocumentDto toDto(ManagerDocument document) {
    return new ManagerDocumentDto(
        document.getDocumentType(),
        document.getDocumentUrl(),
        document.getUploadedAt()
    );
  }
}
