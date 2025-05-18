package com.example.homeaid.global.common.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ApiResponse<T> {

  private boolean success;

  private T data;

  private String code;

  private String message;

  public static <T> ApiResponse<T> success(T data) {
    return ApiResponse.of(true, data, "SUCCESS", "요청이 성공했습니다.");
  }

  public static ApiResponse<Void> success() {
    return ApiResponse.of(true, null, "SUCCESS", "요청이 성공했습니다.");
  }

  public static ApiResponse<Void> fail(String code, String message) {
    return ApiResponse.of(false, null, code, message);
  }


}
