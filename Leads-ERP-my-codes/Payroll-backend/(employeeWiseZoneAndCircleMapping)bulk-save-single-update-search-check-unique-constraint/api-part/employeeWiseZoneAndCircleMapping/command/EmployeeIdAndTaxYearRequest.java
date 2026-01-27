package com.leads.microcube.tax.employeeWiseZoneAndCircleMapping.command;

import lombok.Data;

@Data
public class EmployeeIdAndTaxYearRequest {
    private String employeeId;
    private Integer taxYear;
}
