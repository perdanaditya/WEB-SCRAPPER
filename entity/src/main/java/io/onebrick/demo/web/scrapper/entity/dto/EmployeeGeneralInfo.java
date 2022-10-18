package io.onebrick.demo.web.scrapper.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeGeneralInfo {

  private String name;
  private String ktpNumber;
  private String bpjsCardNumber;
  private String dob;
  private String phoneNumber;
  private String address;
  private String gender;
  private String totalBalance;
}
