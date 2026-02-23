package com.leads.microcube.payroll.bonusDisbursement.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BonusDisbursementSearchRequest {
    private String empOrgId;
    private String empBranchId;
    private Integer bonusYear;
    private String bonusType;
    private LocalDate bonusDate;
    private int pageIndex;
    private int pageSize;
}
