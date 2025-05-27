package com.homeaid.common.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "공통 API 응답 포맷")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommonApiResponse<T> {

  @Schema(description = "성공 여부", example = "fail")
  private boolean success;

  @Schema(description = "응답 데이터", nullable = true)
  private T data;

  @Schema(description = "응답 코드", example = "INVALID_REQUEST", nullable = true)
  private String code;

  @Schema(description = "응답 메시지", example = "잘못된 요청입니다", nullable = true)
  private String message;

  public static <T> CommonApiResponse<T> of(boolean success, T data, String code, String message) {
    return new CommonApiResponse<>(success, data, code, message);
  }

  public static <T> CommonApiResponse<T> success(T data) {
    return CommonApiResponse.of(true, data, "SUCCESS", "요청이 성공했습니다.");
  }

  public static CommonApiResponse<Void> success() {
    return CommonApiResponse.of(true, null, "SUCCESS", "요청이 성공했습니다.");
  }

  public static CommonApiResponse<Void> fail(String code, String message) {
    return CommonApiResponse.of(false, null, code, message);
  }


}
