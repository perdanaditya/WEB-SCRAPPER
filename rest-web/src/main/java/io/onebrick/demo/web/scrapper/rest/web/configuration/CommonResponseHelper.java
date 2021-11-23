package io.onebrick.demo.web.scrapper.rest.web.configuration;


import io.onebrick.demo.web.scrapper.service.response.BaseResponse;

public class CommonResponseHelper {

  private CommonResponseHelper() {
  }

  @SuppressWarnings("unchecked")
  public static <T> BaseResponse<T> constructSuccessResponse(T data) {
    return (BaseResponse.<T>builder()
        .status(200)
        .message("SUCCESS")
        .data(data)
        .build());
  }
}
