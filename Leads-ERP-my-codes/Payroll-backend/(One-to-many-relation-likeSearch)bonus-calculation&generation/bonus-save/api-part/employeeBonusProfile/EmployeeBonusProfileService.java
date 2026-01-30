package com.leads.microcube.payroll.employeeBonusProfile;

import com.leads.microcube.payroll.employeeBonusProfile.command.EmployeeBonusProfileRequest;
import com.leads.microcube.payroll.employeeBonusProfile.query.EmployeeBonusProfileResponse;


public interface EmployeeBonusProfileService {
    EmployeeBonusProfileResponse bonusCreate(EmployeeBonusProfileRequest request);

//    EmployeeBonusProfileResponse updateMapping(EmployeeBonusProfileRequest requests);

}
