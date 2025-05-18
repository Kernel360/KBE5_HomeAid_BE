package com.example.homeaid.global.common.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "공통 API 응답 포맷")
@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ApiResponse<T> {

  @Schema(description = "성공 여부", example = "true")
  private boolean success;

  @Schema(description = "응답 데이터", nullable = true)
  private T data;

  @Schema(description = "응답 코드", example = "SUCCESS", nullable = true)
  private String code;

  @Schema(description = "응답 메시지", example = "요청이 성공했습니다.", nullable = true)
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
