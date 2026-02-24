package com.leads.microcube.payroll.organizationBankInformation.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationBankInfoSearchRequest {
    private Long companyId;
    private Long branchId;
    private Long bankName;
    private Long bankBranch;
    private int pageIndex;
    private int pageSize;
}
