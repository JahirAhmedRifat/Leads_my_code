package com.leads.microcube.payroll.employeeBonusProfile;

import com.leads.microcube.payroll.employeeBonusProfile.command.EmployeeBonusProfileRequest;
import com.leads.microcube.payroll.employeeBonusProfile.query.EmployeeBonusProfileResponse;

import java.util.List;

public interface EmployeeBonusProfileService {
    List<EmployeeBonusProfileResponse> bulkSave(List<EmployeeBonusProfileRequest> requests);
//    EmployeeBonusProfileResponse updateMapping(EmployeeBonusProfileRequest requests);

}
