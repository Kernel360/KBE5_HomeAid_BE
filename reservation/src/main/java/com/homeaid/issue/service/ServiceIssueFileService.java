package com.homeaid.issue.service;

import com.homeaid.issue.domain.ServiceIssue;
import java.io.FileNotFoundException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ServiceIssueFileService {

  void uploadFiles(ServiceIssue serviceIssue, List<MultipartFile> files);

  void updateFiles(ServiceIssue serviceIssue, List<MultipartFile> files, List<Long> deleteImageIds);

  void deleteFiles(ServiceIssue serviceIssue) throws FileNotFoundException;

}
