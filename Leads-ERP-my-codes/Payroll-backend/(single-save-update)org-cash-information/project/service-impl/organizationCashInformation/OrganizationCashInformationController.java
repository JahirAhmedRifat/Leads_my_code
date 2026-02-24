package com.leads.microcube.payroll.organizationCashInformation;

import com.leads.microcube.base.BaseController;
import com.leads.microcube.base.BaseQueryService;
import com.leads.microcube.base.BaseService;
import com.leads.microcube.base.query.CommonResponse;
import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.infrastructure.anotation.ApiController;
import com.leads.microcube.payroll.organizationCashInformation.command.OrganizationCashInfoSearchRequest;
import com.leads.microcube.payroll.organizationCashInformation.command.OrganizationCashInformationRequest;
import com.leads.microcube.payroll.organizationCashInformation.query.OrganizationCashInformationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

@ApiController
@RequestMapping("org-cash-information")
public class OrganizationCashInformationController
        extends BaseController<OrganizationCashInformationRequest, OrganizationCashInformationResponse>
        implements OrganizationCashInformationApi {

    private final OrganizationCashInformationQueryService cashInformationQueryService;
    private final OrganizationCashInformationService cashInformationService;

    public OrganizationCashInformationController(
            BaseService<OrganizationCashInformationRequest, OrganizationCashInformationResponse> service,
            BaseQueryService<OrganizationCashInformationRequest, OrganizationCashInformationResponse> queryService,
            OrganizationCashInformationQueryService cashInformationQueryService,
            OrganizationCashInformationService cashInformationService
    ) {
        super(service, queryService);
        this.cashInformationQueryService = cashInformationQueryService;
        this.cashInformationService = cashInformationService;
    }

    @Override
    public ResponseEntity<?> searchCriteria(OrganizationCashInfoSearchRequest request) {
        PageResponse<OrganizationCashInformationResponse> result = cashInformationQueryService.searchCriteria(request);

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