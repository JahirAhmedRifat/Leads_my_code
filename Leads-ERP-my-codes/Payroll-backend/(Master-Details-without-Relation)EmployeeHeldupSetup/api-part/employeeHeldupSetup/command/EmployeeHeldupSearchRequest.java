package com.leads.microcube.payroll.employeeHeldupSetup.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeHeldupSearchRequest {
    private String employeeId;
    private Long heldupType;
    private LocalDate fromDate;
    private LocalDate toDate;
    private int pageIndex;
    private int pageSize;
}
