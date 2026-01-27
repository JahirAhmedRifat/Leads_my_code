package com.leads.microcube.payroll.yearlyBonusSetup.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leads.microcube.base.command.IdHolder;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class YearlyBonusSetupRequest extends IdHolder {

    @JsonProperty("isDeleted")
    private Boolean isDeleted;

    @NotNull(message = "Bonus Year is required")
    @Min(value = 1000, message = "Bonus Year must be a 4-digit year")
    @Max(value = 9999, message = "Bonus Year must be a 4-digit year")
    private Integer bonusYear;

    @NotNull(message = "Bonus policy is required")
    private Long bonusPolicy;

    @NotNull(message = "Bonus type is required")
    private String bonusType;

    @NotNull(message = "Bonus date is required")
    private LocalDate bonusDate;

    private Boolean allReligions;
    private Long religion;
    private Boolean taxable;
    private Boolean isAdjustWithSalary;

//    @NotNull(message = "Salary Year is required")
    @Min(value = 1000, message = "Salary Year must be a 4-digit year")
    @Max(value = 9999, message = "Salary Year must be a 4-digit year")
    private Integer salaryYear;

//    @NotBlank(message = "Salary month is required")
    private String salaryMonth;

    private Boolean isAppraisalRatio;
    private Long appraisalPeriod;
    private String appraisalPhase;
}
