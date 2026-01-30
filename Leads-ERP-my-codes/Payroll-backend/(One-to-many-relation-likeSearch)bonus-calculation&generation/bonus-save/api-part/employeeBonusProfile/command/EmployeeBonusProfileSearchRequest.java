package com.leads.microcube.payroll.employeeBonusProfile.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeBonusProfileSearchRequest {
    private String empOrgId;
    private String empBranchId;
    private Integer bonusYear;
    private String bonusType;
    private LocalDate bonusDate;
    private int pageIndex;
    private int pageSize;
}
