package io.onebrick.demo.web.scrapper.entity.dto;

import lombok.Data;

@Data
public class SalaryInfo {
    private String companyName;
    private String monthName;
    private String salary;

    @Override
    public String toString() {
        return "SalaryInfo{" +
                "companyName='" + companyName + '\'' +
                ", monthName='" + monthName + '\'' +
                ", salary='" + salary + '\'' +
                '}';
    }
}
