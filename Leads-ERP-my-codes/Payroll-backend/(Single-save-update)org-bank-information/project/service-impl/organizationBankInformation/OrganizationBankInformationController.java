package com.leads.microcube.payroll.organizationBankInformation;


import com.leads.microcube.base.BaseController;
import com.leads.microcube.base.BaseQueryService;
import com.leads.microcube.base.BaseService;
import com.leads.microcube.base.query.CommonResponse;
import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.infrastructure.anotation.ApiController;
import com.leads.microcube.payroll.organizationBankInformation.command.OrganizationBankInfoSearchRequest;
import com.leads.microcube.payroll.organizationBankInformation.command.OrganizationBankInformationRequest;
import com.leads.microcube.payroll.organizationBankInformation.query.OrganizationBankInformationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

@ApiController
@RequestMapping("org-bank-information")
public class OrganizationBankInformationController
        extends BaseController<OrganizationBankInformationRequest, OrganizationBankInformationResponse>
        implements OrganizationBankInformationApi {

    private final OrganizationBankInformationQueryService bankInformationQueryService;
    private final OrganizationBankInformationService bankInformationService;

    public OrganizationBankInformationController(
            BaseService<OrganizationBankInformationRequest, OrganizationBankInformationResponse> service,
            BaseQueryService<OrganizationBankInformationRequest, OrganizationBankInformationResponse> queryService,
            OrganizationBankInformationQueryService bankInformationQueryService,
            OrganizationBankInformationService bankInformationService
    ) {
        super(service, queryService);
        this.bankInformationQueryService = bankInformationQueryService;
        this.bankInformationService = bankInformationService;
    }

    @Override
    public ResponseEntity<?> searchCriteria(OrganizationBankInfoSearchRequest request) {
        PageResponse<OrganizationBankInformationResponse> result = bankInformationQueryService.searchCriteria(request);

        if (result.getContent().isEmpty()) {
            return ResponseEntity.ok(
                    CommonResponse.makeResponse(
                            null,
                            "No Data found",
                            false
                    )
            );
        }

        return ResponseEntity.ok(
                CommonResponse.makeResponse(
                        result,
                        "Data fetched successfully",
                        true
                )
        );
    }
}