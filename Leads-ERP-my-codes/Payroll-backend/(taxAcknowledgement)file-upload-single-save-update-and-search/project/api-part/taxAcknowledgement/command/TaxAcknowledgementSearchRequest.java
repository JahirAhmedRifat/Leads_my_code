package com.leads.microcube.tax.taxAcknowledgement.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaxAcknowledgementSearchRequest {
    private String employeeId;
    private Integer taxYear;
    private int pageIndex = 0;
    private int pageSize = 25;
}
