package io.onebrick.demo.web.scrapper.entity.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeInfo {
  private String username;
  private String userAccessToken;

  private String name;
  private String ktpNumber;
  private String bpjsCardNumber;
  private String dob;
  private String phoneNumber;
  private String address;
  private String gender;
  private String totalBalance;

  private String employmentInfos;
  private String salaryInfos;
//  private EmployeeGeneralInfo employeeGeneralInfo;
//  private List<EmploymentInfo> employmentInfos;
//  private List<SalaryInfo> salaryInfos;

}
