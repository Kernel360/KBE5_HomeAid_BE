package com.homeaid.service;

import com.homeaid.domain.Manager;
import com.homeaid.domain.ManagerDocument;
import com.homeaid.dto.request.ManagerDetailInfoRequestDto;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ManagerService {

  void saveManagerDetailInfo(Long managerId, ManagerDetailInfoRequestDto requestDto);

  void uploadManagerFiles(Long managerId, MultipartFile idFile, MultipartFile criminalFile,
      MultipartFile healthFile)
      throws IOException;

  Manager getUploadManagerFiles(Long managerId);

  Manager getManagerById(Long id);
}
