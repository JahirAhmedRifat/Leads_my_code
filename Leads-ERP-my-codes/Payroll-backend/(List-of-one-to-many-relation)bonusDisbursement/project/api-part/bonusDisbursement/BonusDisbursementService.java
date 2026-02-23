package com.leads.microcube.payroll.bonusDisbursement;

import com.leads.microcube.payroll.bonusDisbursement.command.BonusDisbursementRequest;
import com.leads.microcube.payroll.bonusDisbursement.query.BonusDisbursementResponse;

import java.util.List;


public interface BonusDisbursementService {
    List<BonusDisbursementResponse> bulkSave(List<BonusDisbursementRequest> requests);
//    EmployeeBonusProfileResponse updateMapping(EmployeeBonusProfileRequest requests);

}
