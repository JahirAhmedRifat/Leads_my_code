package com.leads.microcube.payroll.yearlyBonusSetup.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class YearlyBonusSetupSearchRequest {
    private Integer bonusYear;
    private Long bonusPolicy;
    private String bonusType;
    private int pageIndex = 0;
    private int pageSize = 25;
}
