package io.onebrick.demo.web.scrapper.entity.dto;

import lombok.Data;

@Data
public class EmploymentInfo {
    private String latestSalary;
    private String companyName;
    private String latestPaymentDate;
    private String workingMonth;
    private String status;

    @Override
    public String toString() {
        return "EmploymentInfo{" +
            "latestSalary='" + latestSalary + '\'' +
            ", companyName='" + companyName + '\'' +
            ", latestPaymentDate='" + latestPaymentDate + '\'' +
            ", workingMonth='" + workingMonth + '\'' +
            ", status='" + status + '\'' +
            '}';
    }
}
