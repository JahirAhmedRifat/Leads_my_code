package com.leads.microcube.payroll.organizationCashInformation;

import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.payroll.organizationCashInformation.command.OrganizationCashInfoSearchRequest;
import com.leads.microcube.payroll.organizationCashInformation.query.OrganizationCashInformationResponse;

public interface OrganizationCashInformationQueryService {
    PageResponse<OrganizationCashInformationResponse> searchCriteria(OrganizationCashInfoSearchRequest request);
}