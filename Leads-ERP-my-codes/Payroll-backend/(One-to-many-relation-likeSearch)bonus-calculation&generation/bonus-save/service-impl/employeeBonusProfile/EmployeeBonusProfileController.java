package com.leads.microcube.payroll.employeeBonusProfile;

import com.leads.microcube.base.BaseController;
import com.leads.microcube.base.BaseQueryService;
import com.leads.microcube.base.BaseService;
import com.leads.microcube.base.query.CommonResponse;
import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.infrastructure.anotation.ApiController;
import com.leads.microcube.payroll.employeeBonusProfile.command.EmployeeBonusProfileRequest;
import com.leads.microcube.payroll.employeeBonusProfile.command.EmployeeBonusProfileSearchRequest;
import com.leads.microcube.payroll.employeeBonusProfile.query.EmployeeBonusProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@ApiController
@RequestMapping("employee_bonus_profile")
public class EmployeeBonusProfileController
        extends BaseController<EmployeeBonusProfileRequest, EmployeeBonusProfileResponse>
        implements EmployeeBonusProfileApi {

    private final EmployeeBonusProfileService bonusProfileService;
    private final EmployeeBonusProfileQueryService profileQueryService;

    public EmployeeBonusProfileController(
            BaseService<EmployeeBonusProfileRequest, EmployeeBonusProfileResponse> service,
            BaseQueryService<EmployeeBonusProfileRequest, EmployeeBonusProfileResponse> queryService,
            EmployeeBonusProfileService bonusProfileService,
            EmployeeBonusProfileQueryService profileQueryService
    ) {
        super(service, queryService);
        this.bonusProfileService = bonusProfileService;
        this.profileQueryService = profileQueryService;
    }

    @Override
    public ResponseEntity<?> bonusCreate(EmployeeBonusProfileRequest request) {

        EmployeeBonusProfileResponse result = bonusProfileService.bonusCreate(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponse.makeResponse(
                        result,
                        "Bonus profile created successfully",
                        true
                )
        );
    }

    @Override
    public ResponseEntity<?> searchCriteria(EmployeeBonusProfileSearchRequest request) {
        PageResponse<EmployeeBonusProfileResponse> result = profileQueryService.searchCriteria(request);

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
