package com.leads.microcube.payroll.employeeBonusProfile.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leads.microcube.base.command.IdHolder;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeBonusProfileRequest extends IdHolder {

    @JsonProperty("isDeleted")
    private Boolean isDeleted;

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @NotNull(message = "Designation is required")
    private Long designation;

    @NotNull(message = "Department is required")
    private Long department;

    @NotNull(message = "Bonus Amount is required")
    private BigDecimal bonusAmount;

    @NotNull(message = "Bonus date is required")
    private LocalDate bonusDate;

    @NotNull(message = "Bonus Year is required")
    @Min(value = 1000, message = "Bonus Year must be a 4-digit year")
    @Max(value = 9999, message = "Bonus Year must be a 4-digit year")
    private Integer bonusYear;

    @NotBlank(message = "Yearly Bonus uuid is required")
    private String yearlyBonusUuid;

    private String status;

}
