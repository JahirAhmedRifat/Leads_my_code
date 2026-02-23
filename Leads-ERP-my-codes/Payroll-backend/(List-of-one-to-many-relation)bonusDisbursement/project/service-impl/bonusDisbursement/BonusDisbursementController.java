package com.leads.microcube.payroll.bonusDisbursement;

import com.leads.microcube.base.BaseController;
import com.leads.microcube.base.BaseQueryService;
import com.leads.microcube.base.BaseService;
import com.leads.microcube.base.query.CommonResponse;
import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.infrastructure.anotation.ApiController;
import com.leads.microcube.payroll.bonusDisbursement.command.BonusDisbursementRequest;
import com.leads.microcube.payroll.bonusDisbursement.command.BonusDisbursementSearchRequest;
import com.leads.microcube.payroll.bonusDisbursement.query.BonusDisbursementResponse;
import com.leads.microcube.payroll.employeeBonusProfile.EmployeeBonusProfileApi;
import com.leads.microcube.payroll.employeeBonusProfile.EmployeeBonusProfileQueryService;
import com.leads.microcube.payroll.employeeBonusProfile.EmployeeBonusProfileService;
import com.leads.microcube.payroll.employeeBonusProfile.command.EmployeeBonusProfileRequest;
import com.leads.microcube.payroll.employeeBonusProfile.command.EmployeeBonusProfileSearchRequest;
import com.leads.microcube.payroll.employeeBonusProfile.query.EmployeeBonusProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@ApiController
@RequestMapping("bonus-disbursement")
public class BonusDisbursementController
        extends BaseController<BonusDisbursementRequest, BonusDisbursementResponse>
        implements BonusDisbursementApi {

    private final BonusDisbursementService disbursementService;
    private final BonusDisbursementQueryService disbursementQueryService;

    public BonusDisbursementController(
            BaseService<BonusDisbursementRequest, BonusDisbursementResponse> service,
            BaseQueryService<BonusDisbursementRequest, BonusDisbursementResponse> queryService,
            BonusDisbursementService disbursementService,
            BonusDisbursementQueryService disbursementQueryService
    ) {
        super(service, queryService);
        this.disbursementService = disbursementService;
        this.disbursementQueryService = disbursementQueryService;
    }

    @Override
    public ResponseEntity<?> bulkSave(List<BonusDisbursementRequest> requests) {

        List<BonusDisbursementResponse> result = disbursementService.bulkSave(requests);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponse.makeResponse(
                        result,
                        "Bonus Disbursement created successfully",
                        true
                )
        );
    }

    @Override
    public ResponseEntity<?> searchCriteria(BonusDisbursementSearchRequest request) {
        PageResponse<BonusDisbursementResponse> result = disbursementQueryService.searchCriteria(request);

        if (result.getContent().isEmpty()) {
            return ResponseEntity.ok(
                    CommonResponse.makeResponse(
                            List.of(),
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
