package com.leads.microcube.payroll.employeeBonusProfile.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leads.microcube.base.command.IdHolder;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployeeBonusProfileResponse extends IdHolder {

    @JsonProperty("isDeleted")
    private Boolean isDeleted;
    private String empOrgId;
    private String empBranchId;
    private Integer bonusYear;
    private String bonusType;
    private LocalDate bonusDate;
    private BigDecimal totalAmount;
    private String yearlyBonusUuid;
    private List<EmployeeBonusProfileDetailsResponse> bonusProfileDetails;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class EmployeeBonusProfileDetailsResponse extends IdHolder {
        @JsonProperty("isDeleted")
        private Boolean isDeleted;
        private String employeeId;
        private BigDecimal bonusAmount;
        private String status;
    }
}
