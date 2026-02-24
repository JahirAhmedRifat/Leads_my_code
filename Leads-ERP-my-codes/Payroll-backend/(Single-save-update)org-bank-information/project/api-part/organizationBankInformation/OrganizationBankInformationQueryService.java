package com.leads.microcube.payroll.organizationBankInformation;

import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.payroll.organizationBankInformation.command.OrganizationBankInfoSearchRequest;
import com.leads.microcube.payroll.organizationBankInformation.query.OrganizationBankInformationResponse;

public interface OrganizationBankInformationQueryService {
    PageResponse<OrganizationBankInformationResponse> searchCriteria(OrganizationBankInfoSearchRequest request);
}