package com.leads.microcube.tax.taxRebatePolicy;

import com.leads.microcube.tax.taxRebatePolicy.command.TaxRebatePolicyRequest;
import com.leads.microcube.tax.taxRebatePolicy.query.TaxRebatePolicyResponse;

import java.util.List;

public interface TaxRebatePolicyService {
    List<TaxRebatePolicyResponse> bulkSave(List<TaxRebatePolicyRequest> requests);
    TaxRebatePolicyResponse updateMapping(TaxRebatePolicyRequest requests);
}
