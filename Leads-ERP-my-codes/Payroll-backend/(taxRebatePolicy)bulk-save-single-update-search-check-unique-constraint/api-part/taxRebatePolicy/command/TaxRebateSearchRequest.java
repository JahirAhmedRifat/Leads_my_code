package com.leads.microcube.tax.taxRebatePolicy.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaxRebateSearchRequest {
    private Integer taxYear;
    private Long amountType;
    private int pageIndex;
    private int pageSize;
}
