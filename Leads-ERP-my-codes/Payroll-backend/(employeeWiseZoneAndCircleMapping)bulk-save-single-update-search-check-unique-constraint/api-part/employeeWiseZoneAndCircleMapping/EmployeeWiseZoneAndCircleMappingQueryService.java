package com.leads.microcube.tax.employeeWiseZoneAndCircleMapping;

import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.tax.employeeWiseZoneAndCircleMapping.command.EmployeeIdAndTaxYearRequest;
import com.leads.microcube.tax.employeeWiseZoneAndCircleMapping.query.EmployeeWiseZoneAndCircleMappingResponse;
import com.leads.microcube.tax.taxRebatePolicy.query.TaxRebatePolicyResponse;

public interface EmployeeWiseZoneAndCircleMappingQueryService {
    EmployeeWiseZoneAndCircleMappingResponse getDataByEmployeeIdAndTaxYear(EmployeeIdAndTaxYearRequest request);

    PageResponse<EmployeeWiseZoneAndCircleMappingResponse> searchCriteria(
            Integer taxYear,
            String employeeId,
            Long taxZone,
            Long taxCircle,
            int pageIndex,
            int pageSize
    );

}
