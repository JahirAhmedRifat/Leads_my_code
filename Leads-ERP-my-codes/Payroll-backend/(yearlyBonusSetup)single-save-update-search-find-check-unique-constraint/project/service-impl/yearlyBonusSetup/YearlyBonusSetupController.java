package com.leads.microcube.payroll.yearlyBonusSetup;

import com.leads.microcube.base.BaseController;
import com.leads.microcube.base.BaseQueryService;
import com.leads.microcube.base.BaseService;
import com.leads.microcube.base.query.CommonResponse;
import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.infrastructure.anotation.ApiController;
import com.leads.microcube.payroll.yearlyBonusSetup.command.YearlyBonusSetupRequest;
import com.leads.microcube.payroll.yearlyBonusSetup.command.YearlyBonusSetupSearchRequest;
import com.leads.microcube.payroll.yearlyBonusSetup.query.YearlyBonusSetupResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;


@ApiController
@RequestMapping("/yearly-bonus-setup")
public class YearlyBonusSetupController
        extends BaseController<YearlyBonusSetupRequest, YearlyBonusSetupResponse>
        implements YearlyBonusSetupApi {

    private final YearlyBonusSetupQueryService yearlyBonusSetupQueryService;
    private final YearlyBonusSetupService yearlyBonusSetupService;

    public YearlyBonusSetupController(
            BaseService<YearlyBonusSetupRequest, YearlyBonusSetupResponse> service,
            BaseQueryService<YearlyBonusSetupRequest, YearlyBonusSetupResponse> queryService,
            YearlyBonusSetupQueryService yearlyBonusSetupQueryService,
            YearlyBonusSetupService yearlyBonusSetupService
    ) {
        super(service, queryService);
        this.yearlyBonusSetupQueryService = yearlyBonusSetupQueryService;
        this.yearlyBonusSetupService = yearlyBonusSetupService;
    }


    @Override
    public ResponseEntity<?> searchCriteria(YearlyBonusSetupSearchRequest request) {
        PageResponse<YearlyBonusSetupResponse> result = yearlyBonusSetupQueryService.searchCriteria(
                request.getBonusYear(),
                request.getBonusPolicy(),
                request.getBonusType(),
                request.getPageIndex(),
                request.getPageSize()
        );

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

    @Override
    public ResponseEntity<?> findByBonusYearAndPolicyAndType(YearlyBonusSetupSearchRequest request) {
        YearlyBonusSetupResponse result = yearlyBonusSetupQueryService.findByBonusYearAndPolicyAndType(
                request.getBonusYear(),
                request.getBonusPolicy(),
                request.getBonusType()
        );

        if (result == null) {
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