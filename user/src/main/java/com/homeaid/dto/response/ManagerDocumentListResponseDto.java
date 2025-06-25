package com.homeaid.dto.response;

import com.homeaid.domain.Manager;
import com.homeaid.domain.enumerate.ManagerStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ManagerDocumentListResponseDto (
    @Schema(description = "매니저 활동 승인 상태", example = "PENDING")
    ManagerStatus status,

    @Schema(description = "매니저 첨부 서류")
    List<ManagerDocumentDto> documentList

){
  public static ManagerDocumentListResponseDto toDto(Manager manager) {
    return new ManagerDocumentListResponseDto(
        manager.getStatus(),
        manager.getDocuments().stream()
            .map(ManagerDocumentDto::toDto)
            .toList()
    );
  }
}

