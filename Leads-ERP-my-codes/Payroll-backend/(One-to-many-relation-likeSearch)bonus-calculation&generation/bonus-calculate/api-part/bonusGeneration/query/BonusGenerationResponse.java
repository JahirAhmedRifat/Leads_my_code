package com.leads.microcube.process.bonusGeneration.query;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class BonusGenerationResponse {
    private String empOrgId;
    private String empBranchId;
    private String employeeId;
    private String employeeName;
    private Integer designation;
    private Integer department;
    private BigDecimal bonusAmount;
    private Integer bonusYear;
    private String bonusType;
    private LocalDate bonusDate;
    private String yearlyBonusUuid;
}
