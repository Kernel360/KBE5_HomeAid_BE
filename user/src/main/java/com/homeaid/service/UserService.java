package com.homeaid.service;

import com.homeaid.domain.Customer;
import com.homeaid.domain.Manager;
import com.homeaid.domain.User;
import com.homeaid.dto.request.UserUpdateRequestDto;
import jakarta.validation.Valid;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    Manager signUpManager(@Valid Manager manager);

    Customer signUpCustomer(@Valid Customer customer);

    User getUserById(Long id);

    void updateUserInfo(Long userId, UserUpdateRequestDto dto);

    void uploadManagerFiles(Manager manager, MultipartFile idFile, MultipartFile criminalFile, MultipartFile healthFile)
        throws IOException;
}
