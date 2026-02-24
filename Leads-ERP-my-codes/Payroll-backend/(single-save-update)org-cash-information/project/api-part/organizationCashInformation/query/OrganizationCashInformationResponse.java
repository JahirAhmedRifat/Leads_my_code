package com.leads.microcube.payroll.organizationCashInformation.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leads.microcube.base.command.IdHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationCashInformationResponse extends IdHolder {
    @JsonProperty("isDeleted")
    private Boolean isDeleted;
    private Long companyId;
    private Long branchId;
    private String cashRegName;
    private String cashRegNumber;
    private String status;
}
