package com.leads.microcube.payroll.employeeHeldupSetup.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leads.microcube.base.command.IdHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployeeHeldupResponse extends IdHolder {

    @JsonProperty("isDeleted")
    private Boolean isDeleted;
    private String employeeId;
    private Long heldupType;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String remarks;
    private List<EmployeeHeldupDetailsResponse> heldupDetails;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class EmployeeHeldupDetailsResponse extends IdHolder {
        @JsonProperty("isDeleted")
        private Boolean isDeleted;
        private Long heldupMasterId;
        private Long salaryStructureBreakupId;
        private String salaryHead;
        private BigDecimal amount;
        private Integer heldupValue;
    }
}
