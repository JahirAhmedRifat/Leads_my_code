package com.leads.microcube.payroll.bonusDisbursement.query;

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
public class BonusDisbursementResponse extends IdHolder {

    @JsonProperty("isDeleted")
    private Boolean isDeleted;
    private String empOrgId;
    private String empBranchId;
    private Integer bonusYear;
    private String bonusType;
    private LocalDate bonusDate;
    private BigDecimal totalAmount;
    private LocalDate disbursementDate;
    private List<BonusDisbursementDetailsResponse> bonusDisbursementDetails;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class BonusDisbursementDetailsResponse extends IdHolder {
        @JsonProperty("isDeleted")
        private Boolean isDeleted;
        private String employeeId;
        private BigDecimal bonusAmount;
        private String status;
    }
}
