package io.onebrick.demo.web.scrapper.entity.constant.enums;

/**
 * @author Rizky Perdana
 */
public enum ResponseCode {
  SUCCESS("SUCCESS", "SUCCESS");

  private String code;
  private String message;

  ResponseCode(String code, String message) {
    this.code = code;
    this.message = message;
  }

  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}
