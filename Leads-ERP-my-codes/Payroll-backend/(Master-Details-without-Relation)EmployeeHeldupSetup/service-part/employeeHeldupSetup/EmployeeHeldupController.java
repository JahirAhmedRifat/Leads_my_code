package com.leads.microcube.payroll.employeeHeldupSetup;

import com.leads.microcube.base.BaseController;
import com.leads.microcube.base.BaseQueryService;
import com.leads.microcube.base.BaseService;
import com.leads.microcube.base.query.CommonResponse;
import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.infrastructure.anotation.ApiController;
import com.leads.microcube.payroll.employeeHeldupSetup.command.EmployeeHeldupRequest;
import com.leads.microcube.payroll.employeeHeldupSetup.command.EmployeeHeldupSearchRequest;
import com.leads.microcube.payroll.employeeHeldupSetup.query.EmployeeHeldupResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;


@ApiController
@RequestMapping("employee-heldup")
public class EmployeeHeldupController
        extends BaseController<EmployeeHeldupRequest, EmployeeHeldupResponse>
        implements EmployeeHeldupApi {

    private final EmployeeHeldupQueryService heldupQueryService;
    private final EmployeeHeldupService heldupService;

    public EmployeeHeldupController(
            BaseService<EmployeeHeldupRequest, EmployeeHeldupResponse> service,
            BaseQueryService<EmployeeHeldupRequest, EmployeeHeldupResponse> queryService,
            EmployeeHeldupQueryService heldupQueryService,
            EmployeeHeldupService heldupService
    ) {
        super(service, queryService);
        this.heldupQueryService = heldupQueryService;
        this.heldupService = heldupService;
    }

    @Override
    public ResponseEntity<?> heldUpCreate(EmployeeHeldupRequest request) {

        EmployeeHeldupResponse result = heldupService.heldUpCreate(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponse.makeResponse(
                        result,
                        "Held Up created successfully",
                        true
                )
        );
    }

    @Override
    public ResponseEntity<?> updateHeldup(EmployeeHeldupRequest request) {

        EmployeeHeldupResponse result = heldupService.updateHeldup(request);

        return ResponseEntity.ok(
                CommonResponse.makeResponse(
                        result,
                        "Held Up updated successfully",
                        true
                )
        );
    }

    @Override
    public ResponseEntity<?> getInfoByUuid(String uuid) {
        EmployeeHeldupResponse result = heldupQueryService.getInfoById(uuid);

        return ResponseEntity.ok(
                CommonResponse.makeResponse(
                        result,
                        "Data fetched successfully",
                        true
                )
        );
    }

    @Override
    public ResponseEntity<?> searchCriteria(EmployeeHeldupSearchRequest request) {

        PageResponse<EmployeeHeldupResponse> result = heldupQueryService.searchCriteria(request);

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