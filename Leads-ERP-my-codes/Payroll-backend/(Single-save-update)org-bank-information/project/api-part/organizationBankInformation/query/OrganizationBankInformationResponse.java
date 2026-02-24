package com.leads.microcube.payroll.organizationBankInformation.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leads.microcube.base.command.IdHolder;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationBankInformationResponse extends IdHolder {
    @JsonProperty("isDeleted")
    private Boolean isDeleted;
    private Long companyId;
    private Long branchId;
    private String routingNumber;
    private Long bankName;
    private String bankNm;
    private Long bankBranch;
    private String bankBranchNm;
    private Long accountType;
    private String accountName;
    private String accountNumber;
    private Long currency;
    private String currencyNm;
    private String glAccountNo;
    private String status;
}
