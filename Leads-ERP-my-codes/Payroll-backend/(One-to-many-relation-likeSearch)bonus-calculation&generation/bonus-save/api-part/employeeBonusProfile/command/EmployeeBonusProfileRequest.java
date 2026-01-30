package com.leads.microcube.payroll.employeeBonusProfile.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leads.microcube.base.command.IdHolder;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeBonusProfileRequest extends IdHolder {

    @JsonProperty("isDeleted")
    private Boolean isDeleted;

    @NotBlank(message = "Organization id is required")
    private String empOrgId;
    private String empBranchId;

    @NotNull(message = "Bonus Year is required")
    @Min(value = 1000, message = "Bonus Year must be a 4-digit year")
    @Max(value = 9999, message = "Bonus Year must be a 4-digit year")
    private Integer bonusYear;

    @NotBlank(message = "Bonus Type is required")
    private String bonusType;

    @NotNull(message = "Bonus date is required")
    private LocalDate bonusDate;

    @NotBlank(message = "Yearly Bonus uuid is required")
    private String yearlyBonusUuid;

    @NotNull(message = "At least one bonus profile detail is required")
    private List<EmployeeBonusProfileDetailsRequest> bonusProfileDetails;



    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeBonusProfileDetailsRequest extends IdHolder {

        @JsonProperty("isDeleted")
        private Boolean isDeleted;

        @NotBlank(message = "Employee ID is required")
        private String employeeId;

        @NotNull(message = "Bonus Amount is required")
        private BigDecimal bonusAmount;

        private String status;

    }

}
