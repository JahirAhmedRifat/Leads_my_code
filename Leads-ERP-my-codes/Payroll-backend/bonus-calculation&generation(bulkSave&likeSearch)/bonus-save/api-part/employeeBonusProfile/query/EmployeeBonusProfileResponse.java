package com.leads.microcube.payroll.employeeBonusProfile.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leads.microcube.base.command.IdHolder;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployeeBonusProfileResponse extends IdHolder {
    @JsonProperty("isDeleted")
    private Boolean isDeleted;
    private String employeeId;
    private Long designation;
    private Long department;
    private BigDecimal bonusAmount;
    private LocalDate bonusDate;
    private Integer bonusYear;
    private String yearlyBonusUuid;
    private String status;
}
