package com.leads.microcube.payroll.employeeBonusProfile.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeBonusProfileSearchRequest {
    private String employeeId;
    private Long designation;
    private Long department;
    private Integer bonusYear;
    private int pageIndex;
    private int pageSize;
}
