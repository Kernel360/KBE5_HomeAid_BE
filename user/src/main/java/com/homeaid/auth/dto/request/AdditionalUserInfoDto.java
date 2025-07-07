package com.homeaid.auth.dto.request;

import com.homeaid.domain.enumerate.GenderType;
import com.homeaid.domain.enumerate.UserRole;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdditionalUserInfoDto {
  private String email;
  private String phone;
  private LocalDate birth;
  private GenderType gender;
  private UserRole userRole;
}
