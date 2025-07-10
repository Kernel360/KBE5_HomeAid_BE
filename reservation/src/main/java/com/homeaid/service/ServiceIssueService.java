package com.homeaid.service;

import com.homeaid.domain.ServiceIssue;
import java.io.FileNotFoundException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ServiceIssueService {

  void createIssue(Long reservationId, Long userId, String content, List<MultipartFile> files);

  ServiceIssue getIssueByReservation(Long reservationId, Long userId);

  ServiceIssue updateIssue(Long issueId, Long managerId, String content, List<MultipartFile> files,
      List<Long> deleteImageIds);

  void deleteIssue(Long issueId, Long managerId) throws FileNotFoundException;
}
