package com.leads.microcube.payroll.employeeBonusProfile;

import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.payroll.employeeBonusProfile.command.EmployeeBonusProfileSearchRequest;
import com.leads.microcube.payroll.employeeBonusProfile.query.EmployeeBonusProfileResponse;

public interface EmployeeBonusProfileQueryService {
    PageResponse<EmployeeBonusProfileResponse> searchCriteria(EmployeeBonusProfileSearchRequest request);
}
