package com.leads.microcube.payroll.organizationCashInformation;

import com.leads.microcube.payroll.organizationCashInformation.command.OrganizationCashInfoSearchRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface OrganizationCashInformationApi {
    @PostMapping("/search")
    ResponseEntity<?> searchCriteria(@RequestBody OrganizationCashInfoSearchRequest request);
}
