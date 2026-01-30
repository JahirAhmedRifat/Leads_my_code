package com.leads.microcube.payroll.yearlyBonusSetup.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leads.microcube.base.command.IdHolder;
import lombok.Data;

import java.time.LocalDate;

@Data
public class YearlyBonusSetupResponse extends IdHolder {
    @JsonProperty("isDeleted")
    private Boolean isDeleted;
    private Integer bonusYear;
    private Long bonusPolicy;
    private String bonusType;
    private LocalDate bonusDate;
    private Boolean allReligions;
    private Long religion;
    private Boolean taxable;
    private Boolean isAdjustWithSalary;
    private Integer salaryYear;
    private String salaryMonth;
    private Boolean isAppraisalRatio;
    private Long appraisalPeriod;
    private String appraisalPhase;
}
