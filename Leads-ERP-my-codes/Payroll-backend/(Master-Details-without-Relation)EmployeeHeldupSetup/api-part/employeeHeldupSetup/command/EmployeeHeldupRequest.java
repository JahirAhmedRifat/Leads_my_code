package com.leads.microcube.payroll.employeeHeldupSetup.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leads.microcube.base.command.IdHolder;
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
public class EmployeeHeldupRequest extends IdHolder {

    @JsonProperty("isDeleted")
    private Boolean isDeleted;

    @NotBlank(message = "Employee id is required")
    private String employeeId;

    @NotNull(message = "Heldup Type is required")
    private Long heldupType;

    @NotNull(message = "From date is required")
    private LocalDate fromDate;

    @NotNull(message = "To date is required")
    private LocalDate toDate;

    private String remarks;

    private List<EmployeeHeldupDetailsRequest> heldupDetails;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeHeldupDetailsRequest extends IdHolder {

        @JsonProperty("isDeleted")
        private Boolean isDeleted;
        private Long heldupMasterId;
        private Long salaryStructureBreakupId;
        private String salaryHead;
        private BigDecimal amount;
        private Integer heldupValue;
    }

}
