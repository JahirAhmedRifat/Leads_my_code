package com.leads.microcube.payroll.bonusDisbursement.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leads.microcube.base.command.IdHolder;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BonusDisbursementRequest extends IdHolder {

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

    @NotNull(message = "At least one bonus profile detail is required")
    private List<BonusDisbursementDetailsRequest> bonusDisbursementDetails;



    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BonusDisbursementDetailsRequest extends IdHolder {

        @JsonProperty("isDeleted")
        private Boolean isDeleted;

        @NotBlank(message = "Employee ID is required")
        private String employeeId;

        @NotNull(message = "Bonus Amount is required")
        private BigDecimal bonusAmount;

        private String status;

    }

}
