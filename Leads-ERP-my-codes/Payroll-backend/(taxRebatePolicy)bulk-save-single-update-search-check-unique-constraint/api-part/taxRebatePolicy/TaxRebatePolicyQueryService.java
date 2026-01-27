package com.leads.microcube.tax.taxRebatePolicy;

import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.tax.taxRebatePolicy.query.TaxRebatePolicyResponse;

public interface TaxRebatePolicyQueryService {
    PageResponse<TaxRebatePolicyResponse> searchRebateInfo(Integer taxYear, Long amountType, int pageIndex, int pageSize);
}
