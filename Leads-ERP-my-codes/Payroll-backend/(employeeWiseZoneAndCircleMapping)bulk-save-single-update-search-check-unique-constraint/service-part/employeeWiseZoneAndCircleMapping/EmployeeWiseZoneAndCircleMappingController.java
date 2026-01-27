package com.leads.microcube.tax.employeeWiseZoneAndCircleMapping;

import com.leads.microcube.base.BaseController;
import com.leads.microcube.base.BaseQueryService;
import com.leads.microcube.base.BaseService;
import com.leads.microcube.base.query.CommonResponse;
import com.leads.microcube.base.query.Envelope;
import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.infrastructure.anotation.ApiController;
import com.leads.microcube.tax.employeeWiseZoneAndCircleMapping.command.EmployeeIdAndTaxYearRequest;
import com.leads.microcube.tax.employeeWiseZoneAndCircleMapping.command.EmployeeWiseZoneAndCircleMappingRequest;
import com.leads.microcube.tax.employeeWiseZoneAndCircleMapping.command.EmployeeWiseZoneAndCircleSearchRequest;
import com.leads.microcube.tax.employeeWiseZoneAndCircleMapping.query.EmployeeWiseZoneAndCircleMappingResponse;
import com.leads.microcube.tax.taxRebatePolicy.query.TaxRebatePolicyResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@ApiController
@RequestMapping("/employee-wise-zone-and-circle-mapping")
public class EmployeeWiseZoneAndCircleMappingController
        extends BaseController<EmployeeWiseZoneAndCircleMappingRequest, EmployeeWiseZoneAndCircleMappingResponse>
        implements EmployeeWiseZoneAndCircleMappingApi {

    private final EmployeeWiseZoneAndCircleMappingService employeeWiseZoneAndCircleMappingService;
    private final EmployeeWiseZoneAndCircleMappingQueryService employeeWiseZoneAndCircleMappingQueryService;

    public EmployeeWiseZoneAndCircleMappingController(
            BaseService<EmployeeWiseZoneAndCircleMappingRequest, EmployeeWiseZoneAndCircleMappingResponse> service,
            BaseQueryService<EmployeeWiseZoneAndCircleMappingRequest, EmployeeWiseZoneAndCircleMappingResponse> queryService,
            EmployeeWiseZoneAndCircleMappingService employeeWiseZoneAndCircleMappingService,
            EmployeeWiseZoneAndCircleMappingQueryService employeeWiseZoneAndCircleMappingQueryService
    ) {
        super(service, queryService);
        this.employeeWiseZoneAndCircleMappingService = employeeWiseZoneAndCircleMappingService;
        this.employeeWiseZoneAndCircleMappingQueryService = employeeWiseZoneAndCircleMappingQueryService;
    }

    @Override
    public ResponseEntity<Envelope> bulkSave(List<EmployeeWiseZoneAndCircleMappingRequest> requests) {
        Envelope envelope = new Envelope()
                .setStatus(true)
                .setMessage("Mapping saved successfully")
                .setPayload(employeeWiseZoneAndCircleMappingService.bulkSave(requests));

        return ResponseEntity.ok(envelope);
    }

    @Override
    public ResponseEntity<Envelope> updateMapping(EmployeeWiseZoneAndCircleMappingRequest request) {
        Envelope envelope = new Envelope()
                .setStatus(true)
                .setMessage("Mapping update successfully")
                .setPayload(employeeWiseZoneAndCircleMappingService.updateMapping(request));

        return ResponseEntity.ok(envelope);
    }

    @Override
    public ResponseEntity<Envelope> getDataByEmpIdAndTaxYear(EmployeeIdAndTaxYearRequest request) {
        Envelope envelope = new Envelope()
                .setStatus(true)
                .setMessage("Data fetched successfully")
                .setPayload(employeeWiseZoneAndCircleMappingQueryService.getDataByEmployeeIdAndTaxYear(request));

        return ResponseEntity.ok(envelope);
    }

    @Override
    public ResponseEntity<?> searchCriteria(EmployeeWiseZoneAndCircleSearchRequest request) {
        PageResponse<EmployeeWiseZoneAndCircleMappingResponse> result = employeeWiseZoneAndCircleMappingQueryService.searchCriteria(
                request.getTaxYear(),
                request.getEmployeeId(),
                request.getTaxZone(),
                request.getTaxCircle(),
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

}
