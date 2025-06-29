package com.homeaid.service;

import com.homeaid.domain.ServiceIssue;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ServiceIssueFileService {
  void uploadFiles(ServiceIssue serviceIssue, List<MultipartFile> files);
  void updateFiles(ServiceIssue serviceIssue, List<MultipartFile> files);
  void deleteFiles(ServiceIssue serviceIssue);
}
