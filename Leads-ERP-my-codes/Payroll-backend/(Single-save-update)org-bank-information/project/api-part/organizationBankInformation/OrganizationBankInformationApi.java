package com.leads.microcube.payroll.organizationBankInformation;

import com.leads.microcube.payroll.organizationBankInformation.command.OrganizationBankInfoSearchRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface OrganizationBankInformationApi {
    @PostMapping("/search")
    ResponseEntity<?> searchCriteria(@RequestBody OrganizationBankInfoSearchRequest request);
}
