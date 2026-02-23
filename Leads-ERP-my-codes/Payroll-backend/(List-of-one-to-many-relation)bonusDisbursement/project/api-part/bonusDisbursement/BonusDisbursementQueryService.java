package com.leads.microcube.payroll.bonusDisbursement;

import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.payroll.bonusDisbursement.command.BonusDisbursementSearchRequest;
import com.leads.microcube.payroll.bonusDisbursement.query.BonusDisbursementResponse;

public interface BonusDisbursementQueryService {
    PageResponse<BonusDisbursementResponse> searchCriteria(BonusDisbursementSearchRequest request);
}
