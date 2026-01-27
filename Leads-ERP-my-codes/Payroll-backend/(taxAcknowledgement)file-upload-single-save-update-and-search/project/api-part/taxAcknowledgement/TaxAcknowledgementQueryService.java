package com.leads.microcube.tax.taxAcknowledgement;

import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.tax.taxAcknowledgement.query.TaxAcknowledgementResponse;

public interface TaxAcknowledgementQueryService {

//    TaxacknowledgementResponse getDataByEmployeeIdAndTaxYear(EmployeeIdAndTaxYearRequestabc request);

            PageResponse<TaxAcknowledgementResponse> searchCriteria(
            String employeeId,
            Integer taxYear,
            int pageIndex,
            int pageSize
    );

}
