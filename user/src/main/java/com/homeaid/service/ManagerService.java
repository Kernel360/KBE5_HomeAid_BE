package com.homeaid.service;

import com.homeaid.domain.Manager;
import com.homeaid.domain.enumerate.ManagerStatus;
import com.homeaid.dto.request.ManagerDetailInfoRequestDto;
import java.io.IOException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ManagerService {

  void saveManagerDetailInfo(Long managerId, ManagerDetailInfoRequestDto requestDto);

  void uploadManagerFiles(Long managerId, MultipartFile idFile, MultipartFile criminalFile,
      MultipartFile healthFile)
      throws IOException;
}
