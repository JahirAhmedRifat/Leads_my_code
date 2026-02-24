package com.leads.microcube.payroll.organizationCashInformation.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationCashInfoSearchRequest {
    private Long companyId;
    private Long branchId;
    private String cashRegName;
    private String cashRegNumber;
    private int pageIndex;
    private int pageSize;
}
