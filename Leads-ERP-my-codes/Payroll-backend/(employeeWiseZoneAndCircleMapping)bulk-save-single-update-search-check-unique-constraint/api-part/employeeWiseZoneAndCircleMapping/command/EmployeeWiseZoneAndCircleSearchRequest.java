package com.leads.microcube.tax.employeeWiseZoneAndCircleMapping.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeWiseZoneAndCircleSearchRequest {
    private Integer taxYear;
    private String employeeId;
    private Long taxZone;
    private Long taxCircle;
    private int pageIndex = 0;
    private int pageSize = 25;
}
